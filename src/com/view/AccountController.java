package com.view;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.db.model.DAOException;
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
            mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_ACCOUNT_VIEW);
            
            String packet = mainGUI.readLine();
            String packetArr[] = packet.split("!");
            String packetType = packetArr[0];
            String packetCode = packetArr[1];
            
            if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_ACCOUNT_VIEW))
            {
                String result = packetArr[2];
                
                switch (result)
                {
                    case "1":
                        String account = packetArr[3];
                        String bank = packetArr[4];
                        
                        tf_account.setPromptText(account);
                        tf_bank.setPromptText(bank);
                        
                        break;
                    case "2":
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
            
            mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_ACCOUNT_CHANGE + "`" + bank + "`" + account);
            
            String packet = mainGUI.readLine();
            String packetArr[] = packet.split("`");
            String packetType = packetArr[0];
            String packetCode = packetArr[1];
            
            if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_ACCOUNT_CHANGE))
            {
                String result = packetArr[2];
                switch (result)
                {
                    case "1":
                        mainGUI.alert("수정 완료", "데이터 수정 완료");
                        break;
                    case "2":
                        mainGUI.alert("경고", "가격정보 수정 실패!");
                        break;
                }
            }
            
            mainGUI.alert("수정 완료", "수정이 완료 되었습니다");
        }
        catch (DAOException e)
        {
            mainGUI.alert("오류", "해당 계좌번호가 존재하지 않습니다");
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            mainGUI.alert("오류", "DB 접속 오류");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
