import java.text.SimpleDateFormat;
import java.util.Date;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

@ServerEndpoint("/authhandle")
public class Auth {

	// 웹소켓 통해 json 왔을 떄
    @OnMessage
    public void handleMessage(String message){
        System.out.println("메시지를 받았습니다");
        
//        // json 파싱
//        Gson gson = new Gson();
//        TradingElement tInfo = gson.fromJson(message, TradingElement.class);
//
//        if (tInfo.getStatus() == true) {	// 거래 시작 (DB에 거래 정보 입력)
//            tInfo.insertDB();
//		    TradeMain bot = new TradeMain(tInfo);
//		    bot.start();
//        }
//    
//        else {	// 거래 종료 (DB의 거래 상태, 거래 종료 시간 변경) 
//    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    		DB.Query(String.format(
//    				"update trade set status=0, end_date=\'%s\' where user_id=\'%s\' and bot_name=\'%s\' and status=1" ,
//        			dateFormat.format(new Date()), tInfo.getId(), tInfo.getName()), "insert");		
//    		DB.clean();		
//        }
    }
    
    @OnError
    public void handleError(Throwable t){
        t.printStackTrace();
    }
}