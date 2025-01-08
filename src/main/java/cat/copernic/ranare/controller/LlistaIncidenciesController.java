/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Incidencia;
import cat.copernic.ranare.service.mongodb.DocumentService;
import cat.copernic.ranare.service.mysql.IncidenciaService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.bson.types.ObjectId;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.FileNotFoundException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ngall
 */
@Controller
@RequestMapping("/admin/vehicles/incidencies")
public class LlistaIncidenciesController {

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @GetMapping
    public String listIncidencies(@RequestParam(value = "matricula", required = false) String matricula, Model model) {
        List<Incidencia> incidencies;

        if (matricula != null && !matricula.isEmpty()) {
            incidencies = incidenciaService.findByVehicleMatricula(matricula);
        } else {
            incidencies = incidenciaService.findAll();
        }

        model.addAttribute("incidencies", incidencies);
        model.addAttribute("title", "Indicències");
        model.addAttribute("content", "llista-incidencies :: llistarIncidenciaContent");
        return "admin";
    }

    @GetMapping("/modificar/{id}")
    public String modificarIncidencia(@PathVariable Long id, Model model) {
        Incidencia incidencia = incidenciaService.findById(id);
        if (incidencia != null) {
            model.addAttribute("incidencia", incidencia);
        }
        model.addAttribute("title", "Modificar indicència");
        model.addAttribute("content", "crear-incidencia :: crearIncidenciaContent");
        return "admin";
    }

    @GetMapping("/imatges/{imageId}")
    public ResponseEntity<?> mostrarImatge(@PathVariable String imageId) throws IOException {
        try {
            GridFSFile file = gridFSBucket.find(new org.bson.Document("_id", new ObjectId(imageId))).first();

            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            InputStream stream = gridFSBucket.openDownloadStream(file.getObjectId());
            String filename = file.getFilename();

            String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                mimeType = MediaType.IMAGE_JPEG_VALUE;
            } else if (filename.endsWith(".png")) {
                mimeType = MediaType.IMAGE_PNG_VALUE;
            } else if (filename.endsWith(".gif")) {
                mimeType = MediaType.IMAGE_GIF_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/visualitzar/{id}")
    public String visualitzarImatges(@PathVariable Long id, Model model) {
        Incidencia incidencia = incidenciaService.findById(id);
        if (incidencia != null) {
            model.addAttribute("imatgesIds", incidencia.getImatgesIncidenciesIDs());
        }
        model.addAttribute("title", "Visualitzar Imatges");
        return "mostrar-imatges-incidencies";
    }

    @GetMapping("/descarregar/{id}/{pdfId}")
    public ResponseEntity<byte[]> descarregarPdf(@PathVariable Long id, @PathVariable String pdfId) {
        System.out.println("Descarregar PDF amb ID: " + pdfId);
        try {
            byte[] pdfBytes = documentService.getPdfById(pdfId);

            if (pdfBytes == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "incidencia_" + pdfId + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/descarregarTots/{id}")
    public ResponseEntity<byte[]> descarregarTotsPdfs(@PathVariable Long id) {
        Incidencia incidencia = incidenciaService.findById(id);

        if (incidencia == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> pdfIds = incidencia.getDocumentsIncidenciaId();
        if (pdfIds == null || pdfIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] zipBytes = documentService.getPdfsAsZip(pdfIds);

        if (zipBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "incidencia_pdfs.zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok().headers(headers).body(zipBytes);
    }
}
