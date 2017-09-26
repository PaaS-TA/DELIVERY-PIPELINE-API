package paasta.delivery.pipeline.api.common;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.common
 *
 * @author REX
 * @version 1.0
 * @since 9/13/2017
 */
public class ServiceInstances {

    private String id;
    private String owner;
    private String ciServerUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCiServerUrl() {
        return ciServerUrl;
    }

    public void setCiServerUrl(String ciServerUrl) {
        this.ciServerUrl = ciServerUrl;
    }

    @Override
    public String toString() {
        return "ServiceInstances{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", ciServerUrl='" + ciServerUrl + '\'' +
                '}';
    }

}
