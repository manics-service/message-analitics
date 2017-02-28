package by.tsvrko.manics.service.interfaces.dataimport;

import by.tsvrko.manics.model.dataimport.AuthInfo;
import by.tsvrko.manics.model.dataimport.ChatInfo;

import java.util.List;

/**
 * Created main.by tsvrko on 2/10/2017.
 */
public interface ChatImportService {

    List<ChatInfo> getListOfChats(AuthInfo authInfo) ;

    List<Integer> getChatUsersIds(long chatId, AuthInfo authInfo);
}
