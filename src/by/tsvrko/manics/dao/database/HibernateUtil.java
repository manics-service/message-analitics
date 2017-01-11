package by.tsvrko.manics.dao.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
/**
 * Created by tsvrko on 1/5/2017.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();

        } catch (Throwable e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
