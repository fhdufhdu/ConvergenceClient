package com.view;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SignUp
{
	
	@FXML
	private TextField tf_id;
	
	@FXML
	private PasswordField pf_passwd;
	
	@FXML
	private TextField tf_name;
	
	@FXML
	private TextField tf_phone;
	
	@FXML
	private MenuButton mb_gender;
	
	@FXML
	private MenuItem mi_male;
	
	@FXML
	private MenuItem mi_female;
	
	@FXML
	private Button btn_sign_up;
	
	@FXML
	private DatePicker dp_birth;
	
	@FXML
	private Text t_result;
	
	// 메뉴 선택시 외부 메뉴 이름 변경
	@FXML
	void setFemale(ActionEvent event)
	{
		mb_gender.setText(mi_female.getText());
	}
	
	// 메뉴 선택시 외부 메뉴 이름 변경
	@FXML
	void setMale(ActionEvent event)
	{
		mb_gender.setText(mi_male.getText());
	}
	
	// 회원가입 시도
	@FXML
	void trySignUp(ActionEvent event)
	{
		try
		{
			t_result.setText("");
			String id = tf_id.getText();
			String passwd = pf_passwd.getText();
			String name = tf_name.getText();
			String phone_number = tf_phone.getText();
			String birth = dp_birth.getValue().toString();
			String gender;
			// 성별에 따라 gender 값 세팅
			if (mb_gender.getText().equals("남"))
			{
				gender = "1";
			}
			else
			{
				gender = "0";
			}
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SIGNUP + "`2`" + id + "`" + passwd + "`" + name + "`" + phone_number + "`" + birth + "`" + gender);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SIGNUP))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
							alert("회원가입 완료", "확인을 누르시면 로그인 화면으로 전환됩니다!");
							// 스레드사용으로 1.5초 후 로그인 페이지로 전환
							/*
							 * new Thread(() -> { Platform.runLater(() -> { try { Thread.sleep(1500); Parent root = FXMLLoader .load(SignUp.class.getResource("../view/xml/login.fxml")); Scene scene = new Scene(root, 600, 400); Stage primaryStage = (Stage) btn_sign_up.getScene().getWindow(); primaryStage.setTitle("로그인"); primaryStage.setResizable(false); primaryStage.setScene(scene); primaryStage.show(); } catch (Exception e) { e.printStackTrace(); } }); }).start();
							 */
							Parent root = FXMLLoader.load(SignUp.class.getResource("../view/xml/login.fxml"));
							Scene scene = new Scene(root, 600, 400);
							Stage primaryStage = (Stage) btn_sign_up.getScene().getWindow();
							primaryStage.setTitle("로그인");
							primaryStage.setResizable(false);
							primaryStage.setScene(scene);
							primaryStage.show();
							return;
						case "2":
							mainGUI.alert("회원가입 실패", "회원가입 실패! 아이디가 중복됩니다!");
							return;
					}
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("회원가입 실패", "회원가입 실패! 알맞은 정보를 입력했나요?");
			e.printStackTrace();
		}
	}
	
	private void alert(String head, String msg)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("정보");
		alert.setHeaderText(head);
		alert.setContentText(msg);
		
		alert.showAndWait(); // Alert창 보여주기
	}
}