/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mongodb;

import cat.copernic.ranare.repository.mongodb.ImatgesIncidenciaRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class ImatgesIncidenciaService {
    
    @Autowired
    private ImatgesIncidenciaRepository imatgesIncidenciaRepository;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    public List<String> storeImages(MultipartFile[] imatges) throws IOException {
        List<String> imatgesIds = new ArrayList<>();
        
        if (imatges != null && imatges.length > 0) {
            for (MultipartFile imatge : imatges) {
                String imatgeId = imatgesIncidenciaRepository.storeImage(imatge);
                imatgesIds.add(imatgeId);
            }
        }
        
        return imatgesIds;
    }
    
    public List<String> filterImatgesBuides(List<String> imatgesIds){
        List<String> validImatgesIds = new ArrayList<>();
        
        for(String imageId : imatgesIds){
            ObjectId objectId = new ObjectId(imageId);
            
            GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(objectId)));
            
            if(file != null && file.getLength() > 0){
                validImatgesIds.add(imageId);
            }
        }
        return validImatgesIds;
    }
}
