/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mongodb;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngall
 */
@Service
public class DocumentService {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String saveDocument(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();

        Document metadata = new Document();
        metadata.put("type", file.getContentType());
        metadata.put("filename", file.getOriginalFilename());
        metadata.put("size", file.getSize());
        metadata.put("uploadDate", System.currentTimeMillis());

        GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);

        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), inputStream, options);
        System.out.println("Document guardat amb ID: " + fileId.toString());
        return fileId.toString();
    }

    public byte[] getPdfById(String id) throws FileNotFoundException {
        try {
            System.out.println("Trobant arxiu amb ID: " + id);

            ObjectId objectId = new ObjectId(id);
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));

            if (file == null) {
                throw new FileNotFoundException("Arxiu no trobat amb ID: " + id);
            }

            System.out.println("Arxiu trobat amb ID: " + id + ", nom: " + file.getFilename());

            InputStream inputStream = gridFsTemplate.getResource(file).getInputStream();
            return inputStream.readAllBytes();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al llegir l'arxiu amb ID: " + id, e);
        }
    }

    public byte[] getPdfsAsZip(List<String> pdfIds) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (String pdfId : pdfIds) {
                try {
                    ObjectId objectId = new ObjectId(pdfId);
                    GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));

                    if (file == null) {
                        System.err.println("Arxiu no trobat amb ID: " + pdfId);
                        continue;
                    }

                    System.out.println("Afegint arxiu al ZIP amb ID: " + pdfId + ", nom: " + file.getFilename());

                    InputStream inputStream = gridFsTemplate.getResource(file).getInputStream();
                    ZipEntry zipEntry = new ZipEntry(file.getFilename());
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) >= 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    zipOutputStream.closeEntry();
                } catch (IllegalArgumentException e) {
                    System.err.println("El ID proporcionat no és vàlid: " + pdfId + " -> " + e.getMessage());
                }
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el archivo ZIP", e);
        }
    }
}
