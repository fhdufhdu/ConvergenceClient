package com.view;

import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	
	// 성별 선택
	@FXML
	void setFemale(ActionEvent event)
	{
		mb_gender.setText(mi_female.getText());
	}
	
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
				gender = "1";
			else
				gender = "0";
			
			// 회원가입 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SIGNUP + "`2`" + id + "`" + passwd + "`" + name + "`" + phone_number + "`" + birth + "`" + gender);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				if (packet.equals(Protocol.PT_REQ_LOGIN_INFO)) // 접속 시 서버에서 보내는 로그인 요청 프로토콜 무시하기 위해
					continue;
				String packetArr[] = packet.split("`"); //패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SIGNUP))
				{
					String result = packetArr[2];  // 요청 결과
					switch (result)
					{
						case "1": // 요청 성공 시 로그인 화면으로 전환
							mainGUI.alert("회원가입 완료", "확인을 누르시면 로그인 화면으로 전환됩니다!");
							Parent root = FXMLLoader.load(SignUp.class.getResource("../view/xml/login.fxml"));
							Scene scene = new Scene(root, 600, 400);
							Stage primaryStage = (Stage) btn_sign_up.getScene().getWindow();
							primaryStage.setTitle("로그인");
							primaryStage.setResizable(false);
							primaryStage.setScene(scene);
							primaryStage.show();
							return;
						case "2": // 요청 실패
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
}