package libyacvpro.libya_cv.entities.GeneralPackage;

/**
 * Created by Asasna on 10/15/2017.
 */

public class TypeEntities {

    private String type_id;
    private String type_name;

    public TypeEntities(String type_id, String type_name) {
        this.type_id = type_id;
        this.type_name = type_name;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
}
