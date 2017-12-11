package org.superbiz.moviefun.albums;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private AlbumsUpdateSchedulerBean albumsUpdateSchedulerBean;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, AlbumsUpdateSchedulerBean albumsUpdateSchedulerBean) {
        this.albumsUpdater = albumsUpdater;
        this.albumsUpdateSchedulerBean = albumsUpdateSchedulerBean;
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        DateTime jobStartTime = DateTime.now();
        AlbumsUpdateSchedule mostRecentSchedule = this.albumsUpdateSchedulerBean.getMostRecentSchedule();
        try {
            if(this.albumsUpdateSchedulerBean.shouldRunJob(mostRecentSchedule,jobStartTime))
            {
                mostRecentSchedule.setStatus(AlbumsUpdateSchedule.StatusType_START);
                mostRecentSchedule.setLastDateTime(jobStartTime);
                this.albumsUpdateSchedulerBean.updateAlbumsUpdateSchedule(mostRecentSchedule);

                logger.debug("Starting albums update");
                albumsUpdater.update();

                mostRecentSchedule.setStatus(AlbumsUpdateSchedule.StatusType_COMPLETE);
                this.albumsUpdateSchedulerBean.updateAlbumsUpdateSchedule(mostRecentSchedule);

                logger.debug("Finished albums update");
            }

        } catch (Throwable e) {
            mostRecentSchedule.setStatus(AlbumsUpdateSchedule.StatusType_FAIL);
            this.albumsUpdateSchedulerBean.updateAlbumsUpdateSchedule(mostRecentSchedule);
            logger.error("Error while updating albums", e);
        }
    }
}
