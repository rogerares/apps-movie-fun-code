package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class HomeController {

    private MoviesBean moviesBean;

    public HomeController(MoviesBean moviesBean) {
        this.moviesBean = moviesBean;
    }

    @RequestMapping("/")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/setup2")
    public String getSetup2() {
        return "setup";
    }

    @RequestMapping("/setup")
    public String getSetup(Map<String, Object> model) {
        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        moviesBean.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        moviesBean.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        moviesBean.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        moviesBean.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        moviesBean.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));
        model.put("movies", moviesBean.getMovies());
        return "setup";
    }

    /*
        <%
      List<Movie> movies = moviesBean.getMovies();
      for (Iterator<Movie> iterator = movies.iterator(); iterator.hasNext(); ) {
        Movie movie = (Movie) iterator.next();
    %>
    <tr>
      <td><%=movie.getTitle()%>
      </td>
      <td><%=movie.getDirector()%>
      </td>
      <td><%=movie.getGenre()%>
      </td>
    </tr>

    <%
      }
    %>
     */
}
