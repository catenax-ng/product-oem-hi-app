package net.catena_x.btp.hi.oem.common.database.hi.tables.vehicle;

import net.catena_x.btp.hi.oem.common.database.hi.base.HITableBase;
import net.catena_x.btp.hi.oem.common.database.hi.tables.healthindicators.HealthIndicatorsDAO;
import net.catena_x.btp.hi.oem.common.model.dto.healthindicators.HealthIndicators;
import net.catena_x.btp.hi.oem.util.exceptions.OemHIException;
import net.catena_x.btp.libraries.oem.backend.model.dto.vehicle.Vehicle;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionDefaultUseExisting;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableCreateNew;
import net.catena_x.btp.libraries.util.database.annotations.TransactionSerializableUseExisting;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleTableIntern extends HITableBase {
    @PersistenceContext EntityManager entityManager;

    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private HealthindicatorsTableIntern healthindicatorsTable;

    @TransactionDefaultUseExisting
    public void insertVehicleExternTransaction(@NotNull final Vehicle newVehicle)
            throws OemHIException {
        try {
            vehicleRepository.insert(newVehicle.getVehicleId(), newVehicle.getVan(),
                    newVehicle.getGearboxId(), newVehicle.getProductionDate());
        }
        catch(Exception exception) {
            throw failed("Failed to insert vehicle!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void insertVehicleNewTransaction(@NotNull Vehicle newVehicle) throws OemHIException {
        insertVehicleExternTransaction(newVehicle);
    }

    @TransactionDefaultUseExisting
    public void deleteAllExternTransaction() throws OemHIException {
        try {
            vehicleRepository.deleteAll();
        } catch (final Exception exception) {
            throw failed("Deleting all vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteAllNewTransaction() throws OemHIException {
        deleteAllExternTransaction();
    }

    @TransactionSerializableUseExisting
    public void appendHealthindicatorsExternTransaction(@NotNull String vehicleId,
                                                        @NotNull final HealthIndicators newHealthIndicators)
            throws OemHIException {
        appendHealthindicators(getByIdWithHealthindicatorsExternTransaction(vehicleId),
                newHealthIndicators);
    }

    @TransactionSerializableCreateNew
    public void appendHealthindicatorsNewTransaction(@NotNull String vehicleId,
                                                     @NotNull final HealthIndicators newHealthIndicators)
            throws OemHIException {
        appendHealthindicatorsExternTransaction(vehicleId, newHealthIndicators);
    }

    private void appendHealthindicators(@NotNull final VehicleWithHealthindicatorsDAO vehicle,
                                      @NotNull final HealthIndicators newHealthIndicators)
            throws OemHIException {

        if(firstIsNewer(newHealthIndicators, vehicle.healthIndicators())) {
            final String newHealthIndicatorsId = healthindicatorsTable.updateHealthIndicatorsGetIdExternTransaction(
                    newHealthIndicators);

            try {
                vehicleRepository.updateNewestHealthindicatorsIdByVehicleId(vehicle.vehicle().getVehicleId(),
                        newHealthIndicatorsId);
            } catch (final Exception exception) {
                throw failed("Appending health indicators failed!", exception);
            }
        }
    }

    private boolean firstIsNewer(@NotNull final HealthIndicators newHealthIndicators,
                                 @Nullable final HealthIndicatorsDAO healthIndicators)
            throws OemHIException {

        if(healthIndicators == null) {
            return true;
        }

        return newHealthIndicators.getCalculationSyncCounter() > healthIndicators.getCalculationSyncCounter();
    }

    @TransactionDefaultUseExisting
    public void deleteByIdExternTransaction(@NotNull final String id) throws OemHIException {
        try {
            vehicleRepository.deleteByVehilceId(id);
        }
        catch(final Exception exception) {
            throw failed("Deleting vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByIdNewTransaction(@NotNull final String id) throws OemHIException {
        deleteByIdExternTransaction(id);
    }

    @TransactionDefaultUseExisting
    public void deleteByVanExternTransaction(@NotNull final String van) throws OemHIException {
        try {
            vehicleRepository.deleteByVan(van);
        }
        catch(Exception exception) {
            throw failed("Deleting vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public void deleteByVanNewTransaction(@NotNull final String van) throws OemHIException {
        deleteByVanExternTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO getByIdExternTransaction(@NotNull final String vehicleId) throws OemHIException {
        try {
            return vehicleRepository.queryByVehicleId(vehicleId);
        } catch (final Exception exception) {
            throw failed("Querying vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO getByIdNewTransaction(@NotNull final String vehicleId) throws OemHIException {
        return getByIdExternTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByIdWithHealthindicatorsExternTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators("v.vehicle_id=:vehicle_id")
                    .setParameter("vehicle_id", vehicleId);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByIdWithHealthindicatorsNewTransaction(@NotNull final String vehicleId)
            throws OemHIException {
        return getByIdWithHealthindicatorsExternTransaction(vehicleId);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO findByVanExternTransaction(@NotNull final String van) throws OemHIException {
        try {
            return vehicleRepository.queryByVan(van);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO findByVanNewTransaction(@NotNull final String van) throws OemHIException {
        return findByVanExternTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByVanWithHealthindicatorsExternTransaction(@NotNull final String van)
            throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators("v.van=:van")
                    .setParameter("van", van);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByVanWithHealthindicatorsNewTransaction(
            @NotNull final String van) throws OemHIException {
        return getByVanWithHealthindicatorsExternTransaction(van);
    }

    @TransactionDefaultUseExisting
    public VehicleDAO getByGearboxIdExternTransaction(@NotNull final String gearboxId) throws OemHIException {
        try {
            return vehicleRepository.queryByGearboxId(gearboxId);
        } catch(final Exception exception) {
            throw failed("Querying vehicle by gearbox_id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleDAO getByGearboxIdNewTransaction(@NotNull final String gearboxId) throws OemHIException {
        return getByGearboxIdExternTransaction(gearboxId);
    }

    @TransactionDefaultUseExisting
    public VehicleWithHealthindicatorsDAO getByGearboxIdWithHealthindicatorsExternTransaction(
            @NotNull final String gearboxId) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.gearbox_id=:gearbox_id")
                    .setParameter("gearbox_id", gearboxId);
            return getSingle(executeQueryVehicleHealthindicators(query));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by gearbox_id failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public VehicleWithHealthindicatorsDAO getByGearboxIdWithHealthindicatorsNewTransaction(@NotNull final String gearboxId)
            throws OemHIException {
        return getByGearboxIdWithHealthindicatorsExternTransaction(gearboxId);
    }

    private VehicleWithHealthindicatorsDAO queryAndAppendHealthindicators(final VehicleDAO vehicleFromDB)
            throws OemHIException {
        try {
            final String newestHealthindicatorsId = vehicleFromDB.getNewestHealthindicatorsId();

            if(newestHealthindicatorsId == null) {
                return new VehicleWithHealthindicatorsDAO(vehicleFromDB, null);
            }

            return new VehicleWithHealthindicatorsDAO(vehicleFromDB,
                    healthindicatorsTable.getByIdExternTransaction(
                            vehicleFromDB.getNewestHealthindicatorsId()));
        } catch(final Exception exception) {
            throw failed("Querying vehicle by van failed!", exception);
        }
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getAllExternTransaction() throws OemHIException {
        try {
            return vehicleRepository.queryAll();
        }
        catch(Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getAllNewTransaction() throws OemHIException {
        return getAllExternTransaction();
    }

    @TransactionDefaultUseExisting
    public List<VehicleWithHealthindicatorsDAO> getAllWithHealthindicatorsExternTransaction() throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators();
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleWithHealthindicatorsDAO> getAllWithHealthindicatorsNewTransaction() throws OemHIException {
        return getAllWithHealthindicatorsExternTransaction();
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getUpdatedSinceExternTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        try {
            return vehicleRepository.queryUpdatedSince(updatedSince);
        }
        catch(Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getUpdatedSinceNewTransaction(@NotNull final Instant updatedSince)
            throws OemHIException {
        return getUpdatedSinceExternTransaction(updatedSince);
    }

    @TransactionDefaultUseExisting
    public List<VehicleWithHealthindicatorsDAO> getUpdatedSinceWithHealthindicatorsExternTransaction(
            @NotNull final Instant updatedSince) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.update_timestamp>=:updated_since")
                    .setParameter("updated_since", updatedSince);
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleWithHealthindicatorsDAO> getUpdatedSinceWithHealthindicatorsNewTransaction(
            @NotNull final Instant updatedSince) throws OemHIException {
        return getUpdatedSinceWithHealthindicatorsExternTransaction(updatedSince);
    }

    @TransactionDefaultUseExisting
    public List<VehicleDAO> getProducedBetweenExternTransaction(@NotNull final Instant producedSince,
                                                                @NotNull final Instant producedUntil)
            throws OemHIException {
        try {
            return vehicleRepository.queryByProductionDate(producedSince, producedUntil);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleDAO> getProducedBetweenNewTransaction(@NotNull final Instant producedSince,
                                                             @NotNull final Instant producedUntil)
            throws OemHIException {
        return getProducedBetweenExternTransaction(producedSince, producedUntil);
    }

    @TransactionDefaultUseExisting
    public List<VehicleWithHealthindicatorsDAO> getProducedBetweenWithHealthindicatorsExternTransaction(
            @NotNull final Instant producedSince, @NotNull final Instant producedUntil) throws OemHIException {
        try {
            final NativeQuery<Object[]> query = createJoinQueryVehicleHealthIndicators(
                    "v.production_date>=:produced_since AND v.production_date<=:produced_until")
                    .setParameter("produced_since", producedSince)
                    .setParameter("produced_until", producedUntil);
            return executeQueryVehicleHealthindicators(query);
        }
        catch(final Exception exception) {
            throw failed("Querying vehicles failed!", exception);
        }
    }

    @TransactionDefaultCreateNew
    public List<VehicleWithHealthindicatorsDAO> getProducedBetweenWithHealthindicatorsNewTransaction(
            @NotNull final Instant producedSince, @NotNull final Instant producedUntil) throws OemHIException {
        return getProducedBetweenWithHealthindicatorsExternTransaction(producedSince, producedUntil);
    }

    private List<VehicleWithHealthindicatorsDAO> executeQueryVehicleHealthindicators(
            @NotNull final NativeQuery<Object[]> query) throws OemHIException {
        try {
            final List<Object[]> results = query.list();
            final List<VehicleWithHealthindicatorsDAO> vehicles = new ArrayList<>(results.size());

            results.stream().forEach((record) -> vehicles.add(
                    new VehicleWithHealthindicatorsDAO((VehicleDAO)record[0], (HealthIndicatorsDAO)record[1])));

            return vehicles;
        } catch(final Exception exception) {
            throw failed("Executing query failed!", exception);
        }
    }

    private NativeQuery<Object[]> createJoinQueryVehicleHealthIndicators() throws OemHIException {
        return createJoinQueryVehicleHealthIndicators(null);
    }

    private NativeQuery<Object[]> createJoinQueryVehicleHealthIndicators(@Nullable final String where)
            throws OemHIException {
        String query = "SELECT {v.*}, {h.*} FROM vehicles v " +
                "LEFT OUTER JOIN healthindicators h ON v.newest_healthindicators_id = h.id";

        if(where != null) {
            query += " WHERE " + where;
        }

        return createQuery(query).addEntity("v", VehicleDAO.class)
                .addEntity("t", HealthIndicatorsDAO.class);
    }

    private NativeQuery<Object[]> createQuery(@NotNull final String query) throws OemHIException {
        try {
            return (NativeQuery<Object[]>)((Session)this.entityManager.getDelegate()).createSQLQuery(query);
        }
        catch(Exception exception) {
            throw failed("Initializing query failed!", exception);
        }
    }

    private VehicleWithHealthindicatorsDAO getSingle(@Nullable final List<VehicleWithHealthindicatorsDAO> vehicles)
            throws OemHIException {
        if(vehicles == null) {
            return null;
        }

        if(vehicles.isEmpty()) {
            return null;
        }

        if(vehicles.size() > 1) {
            throw failed("Found more than one vehicle with the same id!");
        }

        return vehicles.get(0);
    }
}
