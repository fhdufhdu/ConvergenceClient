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
	private ObservableList<ScreenDTO> screen_list;
	private ScreenDTO table_row_data;
	private TheaterDTO theater;
	
	private String err_type;
	
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
	
	@FXML
	void addScreen(ActionEvent event) throws Exception
	{
		try
		{
			String name = tf_name.getText();
			String row = tf_row.getText();
			String col = tf_col.getText();
			String capacity = String.valueOf(Integer.valueOf(row) * Integer.valueOf(col));
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_ADD + "`" + theater.getId() + "`" + name + "`" + capacity + "`" + row + "`" + col);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_ADD))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
						{
							// 값 추가 후 각 테이블 및 리스트 초기화
							initList();
							
							// text field 초기화
							clearText();
							return;
						}
						case "2":
						{
							mainGUI.alert("오류", "상영관 등록 실패!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("수정오류", err_type);
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
			
			String name = tf_name.getText();
			String row = tf_row.getText();
			String col = tf_col.getText();
			String capacity = String.valueOf(Integer.valueOf(row) * Integer.valueOf(col));
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_CHANGE + "`" + table_row_data.getId() + "`" + theater.getId() + "`" + name + "`" + capacity + "`" + row + "`" + col);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_CHANGE))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
						{
							initList();
							clearText();
							return;
						}
						case "2":
						{
							mainGUI.alert("오류", "상영관 수정 실패");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("수정오류", err_type);
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void clearTextField(ActionEvent event)
	{
		clearText();
	}
	
	@FXML
	void deleteScreen(ActionEvent event) throws Exception
	{
		try
		{
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
			
			String id = table_row_data.getId();
			
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_SCREEN_DELETE + "`" + id);
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_SCREEN_DELETE))
				{
					String result = packetArr[2];
					switch (result)
					{
						case "1":
						{
							mainGUI.alert("삭제완료", "삭제되었습니다");
							initList();
							
							clearText();
							return;
						}
						case "2":
						{
							mainGUI.alert("오류", "상영관 삭제 실패!");
							return;
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			// 입력값 타입이 맞지 않을때
			mainGUI.alert("수정오류", err_type);
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
			
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_SCREEN_VIEW + "`" + theater.getId());
			
			while (true)
			{
				String packet = mainGUI.readLine();
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_SCREEN_VIEW))
				{
					String result = packetArr[2];
					
					switch (result)
					{
						case "1":
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split("{"); // 각 상영관 별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("|"); // 상영관 별 정보 분할
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
						case "2":
						{
							mainGUI.alert("오류", "상영관 리스트가 없습니다");
							return;
						}
						case "3":
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
	
	// 이전 컨트롤러에서 값 받아오기, 상영관 관리에는 해당하는 영화관 객체가 필요
	void initData(TheaterDTO t)
	{
		try
		{
			err_type = "총 좌석, 최대 행, 최대 열에는 숫자만 입력해주세요!";
			
			theater = t;
			theater_name.setText(theater.getName() + "의 상영관 리스트");
			screen_list = FXCollections.observableArrayList();
			
			initList();
			
			tv_screen.getItems().clear();
			
			tc_name.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
			tc_capacity.setCellValueFactory(cellData -> cellData.getValue().getTotalCapacityProperty());
			tc_row.setCellValueFactory(cellData -> cellData.getValue().getMaxRowProperty());
			tc_col.setCellValueFactory(cellData -> cellData.getValue().getMaxColProperty());
			
			tv_screen.setItems(screen_list);
			
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
	
	private void clearText()
	{
		tf_name.clear();
		tf_row.clear();
		tf_col.clear();
		t_result.setText("");
	}
}
