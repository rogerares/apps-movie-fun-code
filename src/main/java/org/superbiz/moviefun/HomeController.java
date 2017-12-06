package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager platformTransactionManagerForAlbums;
    private final PlatformTransactionManager platformTransactionManagerForMovies;
    private final TransactionDefinition transactionDefinitionForAlbums;
    private final TransactionDefinition transactionDefinitionForMovies;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures, @Qualifier("getPlatformTransactionManagerForAlbums") PlatformTransactionManager platformTransactionManagerForAlbums, @Qualifier("getPlatformTransactionManagerForMovies") PlatformTransactionManager platformTransactionManagerForMovies) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.platformTransactionManagerForAlbums = platformTransactionManagerForAlbums;
        this.platformTransactionManagerForMovies = platformTransactionManagerForMovies;
        transactionDefinitionForAlbums = getNewTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED,TransactionDefinition.ISOLATION_DEFAULT,TransactionDefinition.TIMEOUT_DEFAULT,false,"albums");
        transactionDefinitionForMovies = getNewTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED,TransactionDefinition.ISOLATION_DEFAULT,TransactionDefinition.TIMEOUT_DEFAULT,false,"movies");
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        TransactionStatus transactionStatusMovies = platformTransactionManagerForMovies.getTransaction(transactionDefinitionForMovies);
        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        platformTransactionManagerForMovies.commit(transactionStatusMovies);

        TransactionStatus transactionStatusAlbums = platformTransactionManagerForAlbums.getTransaction(transactionDefinitionForAlbums);
        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
        platformTransactionManagerForAlbums.commit(transactionStatusAlbums);

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    private TransactionDefinition getNewTransactionDefinition(int propagationBehavior, int isolationLevel, int timeout, boolean isReadOnly, String name) {
        return new TransactionDefinition() {
            @Override
            public int getPropagationBehavior() {
                return propagationBehavior;
            }

            @Override
            public int getIsolationLevel() {
                return isolationLevel;
            }

            @Override
            public int getTimeout() {
                return timeout;
            }

            @Override
            public boolean isReadOnly() {
                return isReadOnly;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
