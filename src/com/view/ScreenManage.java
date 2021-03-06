package com.view;

import com.db.model.ScreenDTO;
import com.db.model.TheaterDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class ScreenManage
{
	// 테이블 뷰를 위한 리스트, 객체
	private ObservableList<ScreenDTO> screen_list;
	private ScreenDTO table_row_data;
	
	private TheaterDTO theater; // 현재 관리하는 영화관 객체
	
	@FXML
	private BorderPane bp_parent;
	
	@FXML
	private Text theater_name;
	
	@FXML
	private TableView<ScreenDTO> tv_screen;
	
	@FXML
	private TableColumn<ScreenDTO, String> tc_name;
	
	@FXML
	private TableColumn<ScreenDTO, String> tc_capacity;
	
	@FXML
	private TableColumn<ScreenDTO, String> tc_row;
	
	@FXML
	private TableColumn<ScreenDTO, String> tc_col;
	
	@FXML
	private Text t_result;
	
	@FXML
	private TextField tf_name;
	
	@FXML
	private TextField tf_row;
	
	@FXML
	private TextField tf_col;
	
	@FXML
	private Button btn_add_screen;
	
	@FXML
	private Button btn_change_screen;
	
	@FXML
	private Button btn_delete_screen;
	
	@FXML
	private Button btn_clear;
	
	// 초기화, 이전 컨트롤러에서 값 받아오기
	void initData(TheaterDTO t)
	{
		try
		{
			theater = t;
			theater_name.setText(theater.getName() + "의 상영관 리스트");
			screen_list = FXCollections.observableArrayList();
			
			initList(); // 리스트 초기화
			
			tv_screen.getItems().clear();
			
			// 테이블 뷰 col 설정
			tc_name.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
			tc_capacity.setCellValueFactory(cellData -> cellData.getValue().getTotalCapacityProperty());
			tc_row.setCellValueFactory(cellData -> cellData.getValue().getMaxRowProperty());
			tc_col.setCellValueFactory(cellData -> cellData.getValue().getMaxColProperty());
			
			tv_screen.setItems(screen_list); // 리스트, 테이블 뷰 연결
			
			// 테이블 뷰 row 선택시 이벤트
			tv_screen.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ScreenDTO>()
			{
				@Override
				public void changed(ObservableValue<? extends ScreenDTO> observable, ScreenDTO oldValue, ScreenDTO newValue)
				{
					table_row_data = tv_screen.getSelectionModel().getSelectedItem();
					if (table_row_data != null)
					{
						tf_name.setText(table_row_data.getName());
						tf_row.setText(Integer.toString(table_row_data.getMaxRow()));
						tf_col.setText(Integer.toString(table_row_data.getMaxCol()));
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	// 상영관 추가
	@FXML
	void addScreen(ActionEvent event) throws Exception
	{
		try
		{
			String name = tf_name.getText();
			String row = tf_row.getText();
			String col = tf_col.getText();
			String capacity = String.valueOf(Integer.valueOf(row) * Integer.valueOf(col));
			
			// 관리자 -> 선택 영화관에 상영관 추가 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_ADD + "`" + theater.getId() + "`" + name + "`" + capacity + "`" + row + "`" + col);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_ADD))
				{
					String result = packetArr[2]; // 요청 결과
					switch (result)
					{
						case "1": // 요청 성공 시 리스트 초기화
						{
							initList(); // 값 추가 후 각 테이블 및 리스트 초기화
							clearText(); // 텍스트 초기화
							return;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("오류", "상영관 등록 실패!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e) // 입력값 타입이 맞지 않을때
		{
			mainGUI.alert("수정오류", "최대 행, 최대 열에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
	}
	
	@FXML
	void changeScreen(ActionEvent event) throws Exception
	{
		try
		{
			// 테이블 값이 선택되어 있는지 확인
			if (tv_screen.getSelectionModel().isEmpty())
			{
				mainGUI.alert("수정오류", "수정할 데이터를 선택해주세요");
				return;
			}
			
			// 값 획득
			String name = tf_name.getText();
			String row = tf_row.getText();
			String col = tf_col.getText();
			String capacity = String.valueOf(Integer.valueOf(row) * Integer.valueOf(col));
			
			// 관리자 -> 선택한 상영관 정보 수정 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_CHANGE + "`" + table_row_data.getId() + "`" + theater.getId() + "`" + name + "`" + capacity + "`" + row + "`" + col);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_CHANGE))
				{
					String result = packetArr[2]; // 요청 결과
					switch (result)
					{
						case "1": // 요청 성공 시 리스트 초기화
						{
							initList();
							clearText();
							return;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("오류", "상영관 수정 실패");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e) // 입력값 타입이 맞지 않을때
		{
			mainGUI.alert("수정오류", "최대 행, 최대 열에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
	}
	
	// 텍스트 초기화
	@FXML
	void clearTextField(ActionEvent event)
	{
		clearText();
	}
	
	// 상영관 삭제
	@FXML
	void deleteScreen(ActionEvent event) throws Exception
	{
		try
		{
			// 선택되어있는지 확인
			if (tv_screen.getSelectionModel().isEmpty())
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
			// 선택한 행 상영관 아이디 획득
			String id = table_row_data.getId();
			
			// 관리자 -> 선택한 상영관 삭제 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_DELETE + "`" + id);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_DELETE))
				{
					String result = packetArr[2]; // 요청 결과
					switch (result)
					{
						case "1": // 요청 성공 시 리스트 초기화
						{
							mainGUI.alert("삭제완료", "삭제되었습니다");
							initList();
							clearText();
							return;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("오류", "상영관 삭제 실패!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e) // 입력값 타입이 맞지 않을때
		{
			mainGUI.alert("수정오류", "최대 행, 최대 열에는 숫자만 입력해주세요!");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
	}
	
	// 리스트 초기화
	private void initList()
	{
		try
		{
			screen_list.clear();
			
			// 관리자 -> 상영관 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_SCREEN_VIEW + "`" + theater.getId());
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_SCREEN_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공 시 리스트 추가
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split("\\{"); // 각 상영관 별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 상영관 별 정보 분할
								String id = infoArr[0];
								String theater_id = infoArr[1];
								String name = infoArr[2];
								String capacity = infoArr[3];
								String row = infoArr[4];
								String col = infoArr[5];
								
								screen_list.add(new ScreenDTO(id, theater_id, name, Integer.valueOf(capacity), Integer.valueOf(row), Integer.valueOf(col)));
							}
							return;
						}
						case "2": // 상영관 리스트 비어있음
						{
							return;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "상영관 리스트 요청 실패했습니다");
							return;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "상영관 리스트 요청 실패했습니다.");
			e.printStackTrace();
		}
	}
	
	// 텍스트 초기화
	private void clearText()
	{
		tf_name.clear();
		tf_row.clear();
		tf_col.clear();
		t_result.setText("");
	}
}
