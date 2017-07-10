package app.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.hash;

/**
 * Created by Adrian on 2017-05-24.
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    //TODO: Store uuid more efficiently
    private String uuid = UUID.randomUUID().toString();

    @Override
    public int hashCode() {
        return hash(uuid);
    }

    @Override
    public boolean equals(Object other) {
        return this == other ||
                other instanceof BaseEntity && uuidEquals(this, other);
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    private boolean uuidEquals(BaseEntity thisEntity, Object otherEntity) {
        return Objects.equals(thisEntity.uuid, ((BaseEntity) otherEntity).uuid);
    }
}
