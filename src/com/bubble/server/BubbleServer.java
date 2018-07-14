package com.bubble.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JSR356ʵ��WebSocket�����ַ�ʽ,һ����ʹ��ע���,��һ���Ǽ̳�javax.websocket.Endpoint��,
 * �Ƽ���ʽ��ʹ��ע��(����ʹ��ע��)
 * 
 * @ServerEndpoint ע����һ�����ε�ע�⣬���Ĺ�����Ҫ�ǽ�Ŀǰ���ඨ���һ��websocket��������,
 * ע���ֵ�������ڼ����û����ӵ��ն˷���URL��ַ,�ͻ��˿���ͨ�����URL�����ӵ�WebSocket��������
 */
@ServerEndpoint("/websocket")
public class BubbleServer {
	//���ӳأ���������ÿ���ͻ������ӵĶ���
	private static List<BubbleServer> msgList = new ArrayList<BubbleServer>();
//	private static List<Bomb> bombList = new ArrayList<Bomb>();
	//��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
	private static BombList bombList = new BombList();
	private Session session;
	private Map myMap = new Map();
	private String map = myMap.toString();
	/**
	 * ���ӽ����ɹ����õķ���
	 * @param session  ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
	 */
//	public synchronized void addBomb(JsonObject jsonObject) {
//		int x = jsonObject.get("bomb").getAsJsonObject().get("x").getAsInt();
//		int y = jsonObject.get("bomb").getAsJsonObject().get("y").getAsInt();
//		int power = jsonObject.get("bomb").getAsJsonObject().get("power").getAsInt();
//		Bomb bomb= new Bomb(x,y,power);
//		mapArr[x][y] = power;
//		bombList.add(bomb);
//		System.out.println(bomb.toString());
//	}
//	public synchronized void deleteBomb() {
//		if(bombList.size()>0) {
//    		long time = new Date().getTime();
//    		if(time-bombList.get(0).getTime()>=3000) {
//    			System.out.println("bombing:"+bombList.get(0).toString());
//    			bombList.remove(0);
//    		}
//    	}
//	}
    @OnOpen
    public void onOpen(Session session){
    	this.session = session;
	    msgList.add(this);
	    try {
			this.sendMessage(this.map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.println("�������Ӽ���");
    }
    
    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(){
	    msgList.remove(this);
	    System.out.println("�����ӹر�");
    }
    
    /**
     * �յ��ͻ�����Ϣ����õķ���
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
     */
    @OnMessage
    public void onMessage(String message, Session session) {
	    //System.out.println("���Կͻ��˵���Ϣ:" + message);
	    //Ⱥ����Ϣ
    	JsonObject jsonObject = (JsonObject) new JsonParser().parse(message);
    	if(jsonObject.get("bomb") != null) {
    		//x�൱��������y�൱������
    		int x = jsonObject.get("bomb").getAsJsonObject().get("x").getAsInt();
    		int y = jsonObject.get("bomb").getAsJsonObject().get("y").getAsInt();
    		int power = jsonObject.get("bomb").getAsJsonObject().get("power").getAsInt();
    		Bomb bomb= new Bomb(y,x,power);
    		myMap.setMap(y, x, power);
    		bombList.add(bomb);
    		System.out.println(bomb.toString());
    	}
    	
    	String fire = bombList.delete(myMap);
    		
	    for(BubbleServer item: msgList){
		    try {
		    	if(fire.length()>0) {
		    		item.sendMessage("{\"fire\":["+fire+"]}");
		    		System.out.println("{\"fire\":["+fire+"]}");
		    	}
		    	//System.out.println(message);
		    	item.sendMessage(message);
		    } catch (IOException e) {
			    e.printStackTrace();
			    continue;
		    }
	    }
    }
    
    /**
     * ��������ʱ����
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
	    System.out.println("��������");
	    error.printStackTrace();
    }
    
    /**
     * ������������漸��������һ����û����ע�⣬�Ǹ����Լ���Ҫ��ӵķ�����
     * @param message
     * @throws IOException
     */
    public synchronized void sendMessage(String message) throws IOException{
	    this.session.getBasicRemote().sendText(message);//������Ϣ���ͻ���
	    //this.session.getAsyncRemote().sendText(message);
    }
}