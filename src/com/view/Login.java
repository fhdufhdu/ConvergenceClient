package com.view;

import java.io.IOException;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login
{
	static public String USER_ID;
	
	@FXML
	private TextField tf_id;
	
	@FXML
	private PasswordField pf_passwd;
	
	@FXML
	private Button btn_login;
	
	@FXML
	private Button btn_signup;
	
	@FXML
	private Text t_result;
	
	@FXML // 비밀번호 입력 필드에서 엔터를 눌렀을 때
	void enter(KeyEvent event) throws Exception
	{
		if (event.getCode().equals(KeyCode.ENTER))
		{
			loginTry();
		}
	}
	
	@FXML // 로그인 버튼 눌렀을때
	void login(ActionEvent event) throws Exception
	{
		loginTry();
	}
	
	@FXML // 회원 가입 버튼 눌렀을 때
	void signUp(ActionEvent event)
	{
		try
		{
			// 새로운 윈도우 출력
			Parent root = FXMLLoader.load(Login.class.getResource("./xml/sign_up.fxml"));
			Scene scene = new Scene(root, 600, 400);
			Stage primaryStage = (Stage) btn_login.getScene().getWindow();
			primaryStage.setTitle("회원가입");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 로그인 시도
	void loginTry() throws IOException
	{
		try
		{
			String id = tf_id.getText();
			String passwd = pf_passwd.getText();
			
			mainGUI.writePacket(Protocol.PT_REQ_LOGIN + "`" + id + "`" + passwd);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				
				switch (packetType)
				{
					/*
					 * case Protocol.PT_REQ_LOGIN_INFO: // gui에서 값 가져옴 id = tf_id.getText(); passwd = pf_passwd.getText();
					 * 
					 * mainGUI.writePacket(Protocol.PT_REQ_LOGIN + "`" + id + "`" + passwd); return;
					 */
					case Protocol.PT_RES_LOGIN:
						String result = packetArr[1];
						
						switch (result)
						{
							case "1":
							{
								startWindow("./xml/admin_main.fxml", "관리자 모드");
								USER_ID = id;
								return;
							}
							case "2":
							{
								startWindow("./xml/user_main.fxml", "시네마");
								USER_ID = id;
								return;
							}
							case "3":
							{
								t_result.setText("로그인 실패!");
								return;
							}
						}
				}
			}
		}
		catch (Exception e)
		{
			t_result.setText("로그인 실패!");
			e.printStackTrace();
		}
	}
	
	// 로그인 성공 시 해당하는 메인으로 넘어감
	private void startWindow(String path, String title) throws Exception
	{
		Parent root = FXMLLoader.load(Login.class.getResource(path));
		Scene scene = new Scene(root);
		Stage primaryStage = (Stage) btn_login.getScene().getWindow();
		primaryStage.setTitle(title);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}
