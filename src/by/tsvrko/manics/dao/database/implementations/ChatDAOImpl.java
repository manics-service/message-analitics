package by.tsvrko.manics.dao.database.implementations;

import by.tsvrko.manics.dao.database.interfaces.ChatDAO;
import by.tsvrko.manics.model.dataimport.ChatInfo;
import by.tsvrko.manics.model.hibernate.Chat;
import by.tsvrko.manics.model.hibernate.User;
import by.tsvrko.manics.service.implementations.db.SessionServiceImpl;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static by.tsvrko.manics.dao.database.EncodingUtil.*;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tsvrko on 1/8/2017.
 */

@Repository("chatDAO")
public class ChatDAOImpl implements ChatDAO {

    private SessionFactory sessionFactory;
    private SessionServiceImpl sessionServiceImpl;

    @Autowired
    public ChatDAOImpl(SessionFactory sessionFactory, SessionServiceImpl sessionServiceImpl) {
        this.sessionFactory = sessionFactory;
        this.sessionServiceImpl = sessionServiceImpl;
    }

    private Session openSession() {
        return sessionFactory.openSession();
    }
    private static Logger log = Logger.getLogger(ChatDAOImpl.class.getName());



    @Override
    public boolean addChat(ChatInfo chatInfo, String token){

        Session session = null;
        User user = sessionServiceImpl.getUserSessionByToken(token).getUser();

        try {
            session = openSession();
            session.beginTransaction();
            List<Chat> userChats = getChats(user);
            Iterator iterator = userChats.iterator();
            boolean marker=false;
            while (iterator.hasNext())
            {
                Chat chat = (Chat)iterator.next();
                if(chatInfo.getChatId()==chat.getChatId()){
                    marker=true;
                    break;
                }
            }
            if (!marker){
                Chat chat = new Chat();
                chat.setUser(user);
                chat.setTitle(encodeText(chatInfo.getTitle()));
                session.save(chat);}
            session.getTransaction().commit();
        } catch (HibernateException e) {
            log.debug("can't add user to database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return true;
    }

    @Override
    public boolean deleteChat(Chat chat){
        Session session = null;
        try {
            session = openSession();
            session.beginTransaction();
            session.delete(chat);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.debug("can't add user to database", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return true;
    }

    @Override
    public Chat getChatById(int chatId) {
        Session session = null;
        Chat chat=null;
        try {
            session = openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Chat> criteria = builder.createQuery(Chat.class);
            Root<Chat> from = criteria.from(Chat.class);

            criteria.select(from);
            criteria.where(builder.equal(from.get("chatId"),chatId));

            chat = session.createQuery(criteria).getSingleResult();
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.debug("can't get user from database", e);
        }catch(NoResultException e){
            log.debug("user not found", e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return chat;
    }

    @Override
    public List<Chat> getChats(User user) {
        Session session = null;
        List<Chat> list = new ArrayList<>();
         try {
            session = openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Chat> criteria = builder.createQuery(Chat.class);
            Root<Chat> from = criteria.from(Chat.class);

            criteria.select(from);
            criteria.where(builder.equal(from.get("user"), user.getId()));

            list = session.createQuery(criteria).getResultList();
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.debug("can't get user from database", e);
        }catch(NoResultException e){
            log.debug("user not found", e);

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return list;
    }

}
