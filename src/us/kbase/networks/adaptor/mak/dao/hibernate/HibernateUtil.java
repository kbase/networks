package us.kbase.networks.adaptor.mak.dao.hibernate;

import java.net.URL;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		URL url = HibernateUtil.class.getResource("hibernate.cfg.xml");
		try {			
			sessionFactory = new AnnotationConfiguration().configure(url).buildSessionFactory();
		} catch (Throwable ex) {
			// Log exception!
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static Session getSession() throws HibernateException {
		return sessionFactory.openSession();
	}
	
	public static void main(String[] args) {
		Session session = getSession();
		List<Object> values = session.createSQLQuery("show tables").list();
		for(Object val : values)
		{
			System.out.println(val);
		}
	}
}
