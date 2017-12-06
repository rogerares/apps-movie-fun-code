package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return hikariDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanForAlbums(@Qualifier("albumsDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lcemfBean = new LocalContainerEntityManagerFactoryBean();
        lcemfBean.setDataSource(dataSource);
        lcemfBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        lcemfBean.setPackagesToScan("org.superbiz.moviefun.albums");
        lcemfBean.setPersistenceUnitName("albums");
        return lcemfBean;
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManagerForAlbums(@Qualifier("getLocalContainerEntityManagerFactoryBeanForAlbums") LocalContainerEntityManagerFactoryBean lcemfBean){
        return getPlatformTransactionManager(lcemfBean);
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return hikariDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanForMovies(@Qualifier("moviesDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lcemfBean = new LocalContainerEntityManagerFactoryBean();
        lcemfBean.setDataSource(dataSource);
        lcemfBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        lcemfBean.setPackagesToScan("org.superbiz.moviefun.movies");
        lcemfBean.setPersistenceUnitName("movies");
        return lcemfBean;
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManagerForMovies(@Qualifier("getLocalContainerEntityManagerFactoryBeanForMovies") LocalContainerEntityManagerFactoryBean lcemfBean){
        return getPlatformTransactionManager(lcemfBean);
    }

    private PlatformTransactionManager getPlatformTransactionManager(LocalContainerEntityManagerFactoryBean lcemfBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(lcemfBean.getDataSource());
        jpaTransactionManager.setJpaDialect(lcemfBean.getJpaDialect());
        jpaTransactionManager.setPersistenceUnitName(lcemfBean.getPersistenceUnitName());
        jpaTransactionManager.setJpaPropertyMap(lcemfBean.getJpaPropertyMap());
        return jpaTransactionManager;
    }

    @Bean
    public HibernateJpaVendorAdapter getAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }


}
