package com.view;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.db.model.TheaterDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class TheaterManage implements Initializable
{
	
	private ObservableList<TheaterDTO> theater_list;
	private TheaterDTO table_row_data;
	
	@FXML
	private BorderPane bp_parent;
	
	@FXML
	private TableView<TheaterDTO> tv_theater;
	
	@FXML
	private TableColumn<TheaterDTO, String> tc_name;
	
	@FXML
	private TableColumn<TheaterDTO, String> tc_address;
	
	@FXML
	private TableColumn<TheaterDTO, String> tc_screen;
	
	@FXML
	private TableColumn<TheaterDTO, String> tc_seat;
	
	@FXML
	private Text t_result;
	
	@FXML
	private TextField tf_name;
	
	@FXML
	private TextField tf_address;
	
	@FXML
	private Button btn_add_theater;
	
	@FXML
	private Button btn_change_theater;
	
	@FXML
	private Button btn_delete_theater;
	
	@FXML
	private Button btn_screen_manage;
	
	@FXML
	private Button btn_clear;
	
	// 뷰가 불러와지는 동시에 실행될 것들
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			// 테이블 뷰에 넣을 리스트 세팅
			theater_list = FXCollections.observableArrayList();
			
			// 리스트 초기화
			initList();
			
			// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
			tc_name.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
			tc_address.setCellValueFactory(cellData -> cellData.getValue().getAddressProperty());
			tc_screen.setCellValueFactory(cellData -> cellData.getValue().getTotalScreenProperty());
			tc_seat.setCellValueFactory(cellData -> cellData.getValue().getTotalSeatsProperty());
			
			// 테이블 뷰와 리스트를 연결
			tv_theater.setItems(theater_list);
			
			// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
			tv_theater.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TheaterDTO>()
			{
				@Override
				public void changed(ObservableValue<? extends TheaterDTO> observable, TheaterDTO oldValue, TheaterDTO newValue)
				{
					table_row_data = tv_theater.getSelectionModel().getSelectedItem();
					if (table_row_data != null)
					{
						tf_name.setText(table_row_data.getName());
						tf_address.setText(table_row_data.getAddress());
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 영화관 추가
	@FXML
	void addTheater(ActionEvent event) throws Exception
	{
		try
		{
			String name = tf_name.getText();
			String address = tf_address.getText();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_THEATER_ADD + "`" + name + "`" + address + "`0`0");
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_THEATER_ADD))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
							// 값 추가 후 각 테이블 및 리스트 초기화
							initList();
							// text field 초기화
							clearText();
							return;
						case "2":
							mainGUI.alert("경고", "영화관 등록 실패");
							return;
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("경고", "총 상영관, 총 좌석에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
	}
	
	@FXML
	void changeTheater(ActionEvent event) throws Exception
	{
		try
		{
			// 테이블 값이 선택되어 있는지 확인
			if (tv_theater.getSelectionModel().isEmpty())
			{
				mainGUI.alert("수정오류", "수정할 데이터를 선택해주세요");
				return;
			}
			String id = table_row_data.getId();
			String name = tf_name.getText();
			String address = tf_address.getText();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_THEATER_CHANGE + "`" + id + "`" + name + "`" + address + "`0`0");
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_THEATER_CHANGE))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
							initList();
							
							clearText();
							return;
						case "2":
							mainGUI.alert("경고", "영화관 수정 실패!");
							return;
					}
				}
			}
		}
		catch (SQLException e)
		{
			// DB관련 문제 발생시
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("경고", "총 상영관, 총 좌석에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
	}
	
	@FXML
	void deleteTheater(ActionEvent event) throws Exception
	{
		try
		{
			if (tv_theater.getSelectionModel().isEmpty())
			{
				mainGUI.alert("삭제오류", "삭제할 데이터를 선택해주세요");
				return;
			}
			
			// 삭제할 것인지 재 확인
			ButtonType btnType = mainGUI.confirm("삭제확인", "정말로 삭제하시겠습니까?");
			if (btnType != ButtonType.OK)
			{
				return;
			}
			
			String id = table_row_data.getId();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_THEATER_DELETE + "`" + id);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_THEATER_DELETE))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
							mainGUI.alert("삭제완료", "삭제 완료 되었습니다");
							initList();
							
							clearText();
							return;
						case "2":
							mainGUI.alert("경고", "영화관 삭제 실패!");
							return;
					}
				}
			}
		}
		catch (SQLException e)
		{
			// DB관련 문제 발생시
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("경고", "총 상영관, 총 좌석에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
	}
	
	// 텍스트 필드 초기화
	@FXML
	void clearTextField(ActionEvent event)
	{
		clearText();
	}
	
	// 선택한 영화관의 상영관 관리
	@FXML
	void manageScreen(ActionEvent event)
	{
		try
		{
			if (tv_theater.getSelectionModel().isEmpty())
			{
				mainGUI.alert("오류", "데이터를 선택해주세요");
				return;
			}
			FXMLLoader loader = new FXMLLoader(TheaterManage.class.getResource("./xml/admin_sub_page/screen_manage.fxml"));
			Parent root = (Parent) loader.load();
			ScreenManage controller = loader.<ScreenManage>getController();
			controller.initData(table_row_data);
			
			bp_parent.setCenter(root);
			
			clearText();
		}
		catch (NumberFormatException e)
		{
			// 여긴 타입 안맞을때
			mainGUI.alert("오류", "총 상영관, 총 좌석에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void initList()
	{
		try
		{
			theater_list.clear();
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("!"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_THEATER_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String theaterList = packetArr[3];
							String listArr[] = theaterList.split(","); // 각 영화관 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("`"); // 영화관 별 정보 분할
								String id = infoArr[0];
								String name = infoArr[1];
								String address = infoArr[2];
								String screen = infoArr[3];
								String seat = infoArr[4];
								
								theater_list.add(new TheaterDTO(id, name, address, Integer.parseInt(screen), Integer.parseInt(seat)));
							}
							return;
						}
						case "2":
						{
							mainGUI.alert("오류", "영화관 리스트가 없습니다");
							return;
						}
						case "3":
						{
							mainGUI.alert("오류", "영화관 리스트 요청 실패했습니다");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			t_result.setText("영화관 리스트 요청 실패했습니다.");
			e.printStackTrace();
		}
	}
	
	private void clearText()
	{
		tf_name.clear();
		tf_address.clear();
		t_result.setText("");
	}
}
