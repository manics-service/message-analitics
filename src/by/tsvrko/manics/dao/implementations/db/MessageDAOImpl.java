package by.tsvrko.manics.dao.implementations.db;

import by.tsvrko.manics.dao.interfaces.db.MessageDAO;
import by.tsvrko.manics.model.hibernate.Chat;
import by.tsvrko.manics.model.hibernate.Message;
import by.tsvrko.manics.service.interfaces.db.ChatService;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import static by.tsvrko.manics.dao.EncodingUtil.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created main.by tsvrko on 1/7/2017.
 */


@Repository
public class MessageDAOImpl implements MessageDAO{

    private ChatService chatService;
    private SessionFactory sessionFactory;

    @Autowired
    public MessageDAOImpl(ChatService chatService, SessionFactory sessionFactory) {
        this.chatService = chatService;
        this.sessionFactory = sessionFactory;
    }

    private Session openSession() {
        return sessionFactory.openSession();
    }
    private static Logger log = Logger.getLogger(MessageDAOImpl.class.getName());

    public boolean addAll(ArrayList<Message> list, long chatId){

        Session session = null;
        Chat chat = chatService.getChatById(chatId);

        try {
            session = openSession();
            session.beginTransaction();
            List<Message> dbMessageList = getByChat(chat.getId());

            for(int i=0;i<list.size();i++){
                Message message = list.get(i);

                if(dbMessageList.size()==0||dbMessageList.size()!=0&&message.getDate()>dbMessageList.get(dbMessageList.size()-1).getDate()){
                    message.setChat(chat);
                    message.setBody(encodeText(message.getBody()));
                    session.save(message);

                }
                chat.setMessageList(list);
            }

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
    public List<Message> getByChat(int id) {
        Session session = null;
        List<Message> list = new ArrayList<>();
        try {
            session = openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
            Root<Message> from = criteria.from(Message.class);

            criteria.select(from);
            criteria.where(builder.equal(from.get("chat"),id));

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

    @Override
    public List<Message> getByUser(String userId, long chatId){

        Chat dbChat= chatService.getChatById(chatId);
        if (dbChat!=null){
            Session session = null;
            List <Message> messageList = new ArrayList<>();
            try {
                session = openSession();
                session.beginTransaction();

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
                Root<Message> from = criteria.from(Message.class);


                criteria.select(from);
                criteria.where(builder.equal(from.get("userId"), userId),builder.equal(from.get("chat"),dbChat.getId()));

                messageList = session.createQuery(criteria).getResultList();
                session.getTransaction().commit();

            } catch (HibernateException e) {
                log.debug("can't get userInfo from database", e);
            }catch(NoResultException e){
                log.debug("userInfo not found", e);

            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
            return messageList;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Message> getByUserDate(String userId, long chatId, long date) {

        Chat dbChat= chatService.getChatById(chatId);
        if (dbChat!=null){
            Session session = null;
            List <Message> messageList = new ArrayList<>();
            try {
                session = openSession();
                session.beginTransaction();

                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
                Root<Message> from = criteria.from(Message.class);


                criteria.select(from);
                criteria.where(builder.equal(from.get("userId"), userId),builder.equal(from.get("chat"),dbChat.getId()),builder.lessThanOrEqualTo(from.get("date"),date));

                messageList = session.createQuery(criteria).getResultList();
                session.getTransaction().commit();

            } catch (HibernateException e) {
                log.debug("can't get userInfo from database", e);
            }catch(NoResultException e){
                log.debug("userInfo not found", e);

            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
            return messageList;
        }
        else
        {
            return new ArrayList<>();
        }

    }



}




