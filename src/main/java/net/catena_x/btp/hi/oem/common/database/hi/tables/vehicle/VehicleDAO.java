package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vehicles", uniqueConstraints={@UniqueConstraint(columnNames = {"vehicle_id"}),
        @UniqueConstraint(columnNames = {"van"}),
        @UniqueConstraint(columnNames = {"gearbox_id"})})
@NamedNativeQuery(name = "VehicleDAO.insert",
        query = "INSERT INTO vehicles (vehicle_id, van, gearbox_id, production_date) " +
                "VALUES (:vehicle_id, :van, :gearbox_id, :production_date)")
@NamedNativeQuery(name = "VehicleDAO.updateNewestHealthindicatorsIdByVehicleId",
        query = "UPDATE vehicles SET newest_healthindicators_id=:healthindicators_id, " +
                "update_timestamp=CURRENT_TIMESTAMP WHERE vehicle_id=:vehicle_id")
@NamedNativeQuery(name = "VehicleDAO.deleteAll",
        query = "DELETE FROM vehicles")
@NamedNativeQuery(name = "VehicleDAO.deleteByVehilceId",
        query = "DELETE FROM vehicles WHERE vehicle_id=:vehicle_id")
@NamedNativeQuery(name = "VehicleDAO.deleteByVan",
        query = "DELETE FROM vehicles WHERE van=:van")
@NamedNativeQuery(name = "VehicleDAO.queryAll", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles")
@NamedNativeQuery(name = "VehicleDAO.queryByVehicleId", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles WHERE vehicle_id=:vehicle_id")
@NamedNativeQuery(name = "VehicleDAO.queryByVan", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles WHERE van=:van")
@NamedNativeQuery(name = "VehicleDAO.queryByGearboxId", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles WHERE gearbox_id=:gearbox_id")
@NamedNativeQuery(name = "VehicleDAO.queryUpdatedSince", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles WHERE update_timestamp>=:updated_since")
@NamedNativeQuery(name = "VehicleDAO.queryByProductionDate", resultClass = VehicleDAO.class,
        query = "SELECT * FROM vehicles WHERE production_date>=:produced_since " +
                "AND production_date<=:produced_until")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDAO {
    @Id
    @Column(name="vehicle_id", length=50, nullable=false)
    private String vehicleId;

    @Column(name="van", length=50, nullable=false)
    private String van;

    @Column(name="gearbox_id", length=50, nullable=false)
    private String gearboxId;

    @Column(name="update_timestamp", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable=false)
    private Instant updateTimestamp;

    @Column(name="production_date", nullable=false)
    private Instant productionDate;

    @Column(name="newest_healthindicators_id", length=50)
    private String newestHealthindicatorsId;
}
