package com.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class AccountController implements Initializable
{
    
    @FXML
    private TextField tf_account;
    
    @FXML
    private TextField tf_bank;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        try
        {
        	// 관리자 -> 계좌 정보 요청
            mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_ACCOUNT_VIEW); 
            
            String packet = mainGUI.readLine(); // 계좌 정보 요청 응답 수신
            String packetArr[] = packet.split("`"); // 패킷 분할
            String packetType = packetArr[0];
            String packetCode = packetArr[1];
            
            if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_ACCOUNT_VIEW)) 
            {
                String result = packetArr[2]; // 요청 결과
                
                switch (result)
                {
                    case "1": // 요청 성공 시 데이터 출력
                        String account = packetArr[3];
                        String bank = packetArr[4];
                        
                        tf_account.setPromptText(account);
                        tf_bank.setPromptText(bank);
                        break;
                    case "2": // 요청 실패
                        System.out.println("계좌 정보 출력에 실패하였습니다");
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    @FXML
    void change(ActionEvent event)
    {
        try
        {
            if (tf_bank.getText().equals("") || tf_account.getText().equals(""))
            {
                mainGUI.alert("경고", "모든 데이터를 입력해주세요");
                return;
            }
            String bank = tf_bank.getText();
            String account = tf_account.getText();
            
            // 계좌 정보 수정 요청
            mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_ACCOUNT_CHANGE + "`" + bank + "`" + account); 
            
            String packet = mainGUI.readLine(); // 계좌 정보 수정 요청 응답 수신
            String packetArr[] = packet.split("`"); // 패킷 분항
            String packetType = packetArr[0];
            String packetCode = packetArr[1];
            
            if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_ACCOUNT_CHANGE))
            {
                String result = packetArr[2]; // 요청 결과
                switch (result)
                {
                    case "1": // 요청 성공
                        mainGUI.alert("수정 완료", "데이터 수정 완료");
                        break;
                    case "2": // 요청 실패
                        mainGUI.alert("경고", "가격정보 수정 실패!");
                        break;
                }
            }
            
            mainGUI.alert("수정 완료", "수정이 완료 되었습니다");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
