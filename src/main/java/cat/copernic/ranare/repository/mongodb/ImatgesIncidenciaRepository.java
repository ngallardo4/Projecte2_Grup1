/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mongodb;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.IOException;
import java.io.InputStream;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ngall
 */
@Repository
public class ImatgesIncidenciaRepository {
    
    @Autowired
    private GridFSBucket gridFSBucket;
    
    public String storeImage(MultipartFile file) throws IOException{
        ObjectId fileId = gridFSBucket.uploadFromStream(
            file.getOriginalFilename(),
            file.getInputStream(),
            new GridFSUploadOptions().metadata(null)
        );
        System.out.println("ObjectId guardado: " + fileId.toHexString());
        return fileId.toHexString();
    }
}
