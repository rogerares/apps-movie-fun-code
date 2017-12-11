package org.superbiz.moviefun.albums;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class AlbumsUpdateSchedulerBean {
    @PersistenceContext
    private EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void addAlbumsUpdateSchedule(AlbumsUpdateSchedule albumsUpdateSchedule) {
        logger.debug("inserting new instance");
        entityManager.persist(albumsUpdateSchedule);
    }

//    public AlbumsUpdateSchedule find(long id) {
//        return entityManager.find(AlbumsUpdateSchedule.class, id);
//    }

    public List<AlbumsUpdateSchedule> getAlbumsUpdateSchedule() {
        CriteriaQuery<AlbumsUpdateSchedule> cq = entityManager.getCriteriaBuilder().createQuery(AlbumsUpdateSchedule.class);
        cq.select(cq.from(AlbumsUpdateSchedule.class));
        return entityManager.createQuery(cq).getResultList();
    }

//    @Transactional
//    public void deleteAlbumsUpdateSchedule(AlbumsUpdateSchedule albumsUpdateSchedule) {
//        entityManager.remove(albumsUpdateSchedule);
//    }

    @Transactional
    public void updateAlbumsUpdateSchedule(AlbumsUpdateSchedule albumsUpdateSchedule) {
        entityManager.merge(albumsUpdateSchedule);
    }
    @Transactional
    public AlbumsUpdateSchedule getMostRecentSchedule() {
        List<AlbumsUpdateSchedule> schedules = getAlbumsUpdateSchedule();
        if(schedules == null || schedules.isEmpty()) {
            logger.debug("OURLOGGER: getMostRecent -> create new");
            AlbumsUpdateSchedule newSchedule = new AlbumsUpdateSchedule();
            newSchedule.setStatus(AlbumsUpdateSchedule.StatusType_NEW);
            newSchedule.setLastDateTime(DateTime.now());
            logger.debug("OURLOGGER: getMostRecentSchedule-status: " + newSchedule.getStatus());
            logger.debug("OURLOGGER: getMostRecentSchedule-date: " + newSchedule.getLastDateTime());
            logger.debug("OURLOGGER: getMostRecentSchedule-id: " + newSchedule.getId());
            addAlbumsUpdateSchedule(newSchedule);
            return getMostRecentSchedule();
        }

        AlbumsUpdateSchedule mostRecentSchedule = schedules.get(schedules.size()-1);
        logger.debug("OURLOGGER: getMostRecent -> return latest" + mostRecentSchedule);
        return mostRecentSchedule;
    }

    public boolean shouldRunJob(AlbumsUpdateSchedule mostRecentSchedule,DateTime timeStamp) {
        logger.debug("OURLOGGER: shouldRunJob" + mostRecentSchedule);
        logger.debug("OURLOGGER: shouldRunJob-status: " + mostRecentSchedule.getStatus());
        logger.debug("OURLOGGER: shouldRunJob-date: " + mostRecentSchedule.getLastDateTime());
        logger.debug("OURLOGGER: shouldRunJob-id: " + mostRecentSchedule.getId());
        logger.debug("OURLOGGER: shouldRunJob timeStamp to check" + timeStamp);
        if(mostRecentSchedule.isOfStatus(AlbumsUpdateSchedule.StatusType_FAIL) ||
                mostRecentSchedule.isOfStatus(AlbumsUpdateSchedule.StatusType_NEW)) {
            logger.info("OURLOGGER: shouldRunJob returned true - status was f or n");
            return true;
        }

        logger.debug("OURLOGGER: shouldRunJob is previous status complete? {}",mostRecentSchedule.isOfStatus(AlbumsUpdateSchedule.StatusType_COMPLETE) );
        logger.debug("OURLOGGER: shouldRunJob is db timestamp + 2min ({}) greater than this job timestamp ({})",mostRecentSchedule.getLastDateTime().plusMinutes(1).plusSeconds(55),timeStamp);
        logger.debug("OURLOGGER: shouldRunJob db timeStamp to check + 2min=" + mostRecentSchedule.getLastDateTime().plusMinutes(1).plusSeconds(55).isBefore(timeStamp));
        if(mostRecentSchedule.isOfStatus(AlbumsUpdateSchedule.StatusType_COMPLETE) && mostRecentSchedule.getLastDateTime().plusMinutes(1).plusSeconds(55).isBefore(timeStamp)){
            logger.info("OURLOGGER: shouldRunJob returned true - status was c and over due");
            return true;
        }

        logger.info("OURLOGGER: shouldRunJob returned false");
        return false;
    }
}
