/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mongodb;

import cat.copernic.ranare.entity.mongodb.DocumentacioUsuari;
import cat.copernic.ranare.enums.DocumentState;
import cat.copernic.ranare.enums.DocumentType;
import cat.copernic.ranare.repository.mongodb.DocumentacioUsuariRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Raú
 */
@Service
public class DocumentacioUsuariService {

    @Autowired
    DocumentacioUsuariRepository documentacioUsuariRepository;

    public List<DocumentacioUsuari> obtenirDocumentsActiusPerUsuari(String userId) {
        System.out.println("Buscando documentos activos para el usuario: " + userId);
        List<DocumentacioUsuari> documents = documentacioUsuariRepository.findByUserIdAndDocumentState(userId, DocumentState.ACTIVA);

        if (documents.isEmpty()) {
            System.out.println("No hay documentos activos para el usuario: " + userId);
        } else {
            documents.forEach(doc -> {
                System.out.println("Documento encontrado: " + doc.getDocumentType()
                        + ", Front MIME: " + doc.getFrontFileMimeType()
                        + ", Back MIME: " + doc.getBackFileMimeType());
            });
        }
        return documents;
    }

    public List<DocumentacioUsuari> obtenirHistoricDocuments(String userId) {
        return documentacioUsuariRepository.findByUserIdAndDocumentState(userId, DocumentState.CADUCADA);
    }

    public List<DocumentacioUsuari> obtenirDocumentsPerEstat(String userId, DocumentState estat) {
        return documentacioUsuariRepository.findByUserIdAndDocumentState(userId, estat);
    }

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public void afegirDocument(String userId, String documentType, MultipartFile frontFile, MultipartFile backFile) throws IOException {
        DocumentType docType = DocumentType.valueOf(documentType.toUpperCase());
        String frontMimeType = frontFile.getContentType();
        String backMimeType = backFile.getContentType();

        if (!ALLOWED_MIME_TYPES.contains(frontMimeType) || !ALLOWED_MIME_TYPES.contains(backMimeType)) {
            throw new IllegalArgumentException("Format de fitxer no suportat");
        }

        // Marcar documentos previos como caducados
        List<DocumentacioUsuari> documentsActius = documentacioUsuariRepository.findByUserIdAndDocumentState(userId, DocumentState.ACTIVA);
        for (DocumentacioUsuari doc : documentsActius) {
            if (doc.getDocumentType().equals(docType)) {
                doc.setDocumentState(DocumentState.CADUCADA);
                documentacioUsuariRepository.save(doc);
            }
        }

        // Crear y guardar el nuevo documento
        DocumentacioUsuari nouDocument = new DocumentacioUsuari();
        nouDocument.setUserId(userId);
        nouDocument.setDocumentType(docType);
        nouDocument.setDocumentState(DocumentState.ACTIVA);
        nouDocument.setCreationDate(LocalDateTime.now());
        nouDocument.setFrontFile(frontFile.getBytes());
        nouDocument.setBackFile(backFile.getBytes());
        nouDocument.setFrontFileMimeType(frontMimeType);
        nouDocument.setBackFileMimeType(backMimeType);

        documentacioUsuariRepository.save(nouDocument);
    }

}
