package com.view;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.db.model.MovieDTO;
import com.db.model.ScreenDTO;
import com.db.model.TheaterDTO;
import com.db.model.TimeTableDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class MovieTableManage implements Initializable
{
	// 리스트뷰와 테이블뷰를 위한 리스트
	private ObservableList<TheaterDTO> theater_list;
	private ObservableList<ScreenDTO> screen_list;
	private ObservableList<MovieDTO> movie_list;
	private ObservableList<CustomDTO> custom_list;
	
	// 리스트뷰와 테이블뷰에서 선택한 객체
	private TheaterDTO selectedThea;
	private ScreenDTO selectedScreen;
	private MovieDTO selectedMovie;
	private CustomDTO selectedCustom;
	
	@FXML
	private ListView<TheaterDTO> lv_theater;
	
	@FXML
	private ListView<ScreenDTO> lv_screen;
	
	@FXML
	private ListView<MovieDTO> lv_movie;
	
	@FXML
	private TableView<CustomDTO> tv_timetable;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_theater;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_screen;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_movie;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_start_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_end_time;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_current;
	
	@FXML
	private Text result;
	
	@FXML
	private TextField tf_theater;
	
	@FXML
	private TextField tf_screen;
	
	@FXML
	private TextField tf_movie;
	
	@FXML
	private DatePicker dp_start_date;
	
	@FXML
	private MenuButton mb_hours_start;
	
	@FXML
	private MenuButton mb_hours_end;
	
	@FXML
	private Button btn_search_timetable;
	
	@FXML
	private MenuButton mb_minute_start;
	
	@FXML
	private MenuButton mb_minute_end;
	
	// 초기화
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		try
		{
			theater_list = FXCollections.observableArrayList();
			
			// 관리자 -> 영화관 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_THEATER_VIEW);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_THEATER_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공 시 영화관 리스트에 추가
						{
							String theaterList = packetArr[3];
							String listArr[] = theaterList.split("\\{"); // 각 영화관 별로 리스트 분할
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 영화관 별 정보 분할
								String id = infoArr[0];
								String name = infoArr[1];
								String address = infoArr[2];
								String screen = infoArr[3];
								String seat = infoArr[4];
								
								theater_list.add(new TheaterDTO(id, name, address, Integer.parseInt(screen), Integer.parseInt(seat)));
							}
							// 리스트뷰 세팅
							lv_theater.setItems(FXCollections.observableArrayList());
							lv_theater.getItems().addAll(theater_list);
							lv_theater.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_theater.getSelectionModel().getSelectedItem() == null)
									return;
								selectedThea = lv_theater.getSelectionModel().getSelectedItem();
								lv_theater.setMaxHeight(0);
								lv_theater.getItems().clear();
								tf_theater.setText(selectedThea.getName());
							});
							// 리스트뷰에 어떤 객체가 들어가는지 세팅
							lv_theater.setCellFactory(lv -> new ListCell<TheaterDTO>()
							{
								@Override
								protected void updateItem(TheaterDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getName());
								}
							});
							break;
						}
						case "2": // 영화관 리스트 비어있음
						{
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "영화관 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
			
			// 관리자 -> 영화 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_MOVIE_VIEW + "`%`1976-01-01`2222-01-01`%`%`%`0");
			movie_list = FXCollections.observableArrayList();
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_MOVIE_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공 시 영화 리스트에 추가
						{
							String movieList = packetArr[3];
							String listArr[] = movieList.split("\\{"); // 각 영화별로 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 영화 별 정보 분할
								String mv_id = infoArr[0];
								String mv_title = infoArr[1];
								String mv_release_date = infoArr[2];
								String mv_is_current = infoArr[3];
								String mv_plot = infoArr[4];
								String mv_poster_path = infoArr[5];
								String mv_stillCut_path = infoArr[6];
								String mv_trailer_path = infoArr[7];
								String mv_director = infoArr[8];
								String mv_actor = infoArr[9];
								int mv_min = Integer.parseInt(infoArr[10]);
								
								movie_list.add(new MovieDTO(mv_id, mv_title, mv_release_date, mv_is_current, mv_plot, mv_poster_path, mv_stillCut_path, mv_trailer_path, mv_director, mv_actor, mv_min));
							}
							
							lv_movie.setItems(FXCollections.observableArrayList());
							lv_movie.getItems().addAll(movie_list);
							lv_movie.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_movie.getSelectionModel().getSelectedItem() == null)
									return;
								selectedMovie = lv_movie.getSelectionModel().getSelectedItem();
								lv_movie.setMaxHeight(0);
								lv_movie.getItems().clear();
								tf_movie.setText(selectedMovie.getTitle());
							});
							lv_movie.setCellFactory(lv -> new ListCell<MovieDTO>()
							{
								@Override
								protected void updateItem(MovieDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getTitle());
								}
							});
							break;
						}
						case "2": // 영화 리스트 비어있음
						{
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("영화 리스트", "영화 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						break;
				}
			}
			
			custom_list = FXCollections.observableArrayList();
			
			// 테이블 뷰 초기화
			tv_timetable.getItems().clear();
			
			// 각 테이블뷰 컬럼에 어떠한 값이 들어갈 것인지 세팅
			tc_theater.setCellValueFactory(cellData -> cellData.getValue().getTheater());
			tc_screen.setCellValueFactory(cellData -> cellData.getValue().getScreen());
			tc_movie.setCellValueFactory(cellData -> cellData.getValue().getMovie());
			tc_start_time.setCellValueFactory(cellData -> cellData.getValue().getStartTime());
			tc_end_time.setCellValueFactory(cellData -> cellData.getValue().getEndTime());
			tc_current.setCellValueFactory(cellData -> cellData.getValue().getCurrent());
			
			// 테이블 뷰와 리스트를 연결
			tv_timetable.setItems(custom_list);
			
			// 테이블 뷰 row 선택 시 발생하는 이벤트 지정
			tv_timetable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomDTO>()
			{
				@Override
				public void changed(ObservableValue<? extends CustomDTO> observable, CustomDTO oldValue, CustomDTO newValue)
				{
					selectedCustom = tv_timetable.getSelectionModel().getSelectedItem();
				}
			});
			
			// 검색을 위한 menuitem 세팅
			for (int i = 0; i < 24; i++)
			{
				MenuItem hour_s = new MenuItem(Integer.toString(i + 1) + "시");
				hour_s.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_hours_start.setText(hour_s.getText());
					}
				});
				mb_hours_start.getItems().add(hour_s);
				
				MenuItem hour_e = new MenuItem(Integer.toString(i + 1) + "시");
				hour_e.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_hours_end.setText(hour_e.getText());
					}
				});
				mb_hours_end.getItems().add(hour_e);
			}
			
			for (int i = 0; i <= 12; i++)
			{
				MenuItem minute_s = new MenuItem(Integer.toString(i * 5) + "분");
				minute_s.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_minute_start.setText(minute_s.getText());
					}
				});
				mb_minute_start.getItems().add(minute_s);
				
				MenuItem minute_e = new MenuItem(Integer.toString(i * 5) + "분");
				minute_e.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_minute_end.setText(minute_e.getText());
					}
				});
				mb_minute_end.getItems().add(minute_e);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 상영시간표 추가
	@FXML
	void add(ActionEvent event)
	{
		try
		{
			if (selectedMovie == null || selectedScreen == null || dp_start_date.getValue() == null || mb_hours_start.getText().equals("시간") || mb_minute_start.getText().equals("분") || mb_hours_end.getText().equals("시간") || (mb_minute_end.getText().equals("분")))
			{
				mainGUI.alert("경고", "모든 데이터를 입력해주세요");
				return;
			}
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String date = dp_start_date.getValue() == null ? "1976-01-01 " : dateFormat.format(dp_start_date.getValue()) + " ";
			String start_time = mb_hours_start.getText().equals("시간") ? "00:00:00.0" : (mb_minute_start.getText().equals("분") ? mb_hours_start.getText().replace("시", "") + ":00:00.0" : mb_hours_start.getText().replace("시", "") + ":" + mb_minute_start.getText().replace("분", "") + ":00.0");
			String end_time = mb_hours_end.getText().equals("시간") ? "23:59:00.0" : (mb_minute_end.getText().equals("분") ? mb_hours_end.getText().replace("시", "") + ":00:00.0" : mb_hours_end.getText().replace("시", "") + ":" + mb_minute_end.getText().replace("분", "") + ":00.0");
			
			// 관리자 -> 입력한 데이터에 해당하는 상영시간표 등록 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_TIMETABLE_ADD + "`" + selectedMovie.getId() + "`" + selectedScreen.getId() + "`" + date + start_time + "`" + date + end_time);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_TIMETABLE_ADD))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							initList();
							mainGUI.alert("추가 성공", "상영시간표 추가 성공");
							break;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("추가 실패", "상영시간표 중복 발생");
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("추가 실패", "서버에서 DB접속 오류");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "상영시간표 추가 실패");
			e.printStackTrace();
		}
	}
	
	// 상영시간표 변경
	@FXML
	void change(ActionEvent event)
	{
		try
		{
			if (selectedCustom == null)
			{
				mainGUI.alert("경고", "데이터를 선택해주세요");
				return;
			}
			
			if (selectedMovie == null || selectedScreen == null || dp_start_date.getValue() == null || mb_hours_start.getText().equals("시간") || mb_minute_start.getText().equals("분") || mb_hours_end.getText().equals("시간") || (mb_minute_end.getText().equals("분")))
			{
				mainGUI.alert("경고", "모든 데이터를 입력해주세요");
				return;
			}
			
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String date = dp_start_date.getValue() == null ? "1976-01-01 " : dateFormat.format(dp_start_date.getValue()) + " ";
			String start_time = mb_hours_start.getText().equals("시간") ? "00:00:00.0" : (mb_minute_start.getText().equals("분") ? mb_hours_start.getText().replace("시", "") + ":00:00.0" : mb_hours_start.getText().replace("시", "") + ":" + mb_minute_start.getText().replace("분", "") + ":00.0");
			String end_time = mb_hours_end.getText().equals("시간") ? "23:59:00.0" : (mb_minute_end.getText().equals("분") ? mb_hours_end.getText().replace("시", "") + ":00:00.0" : mb_hours_end.getText().replace("시", "") + ":" + mb_minute_end.getText().replace("분", "") + ":00.0");
			
			// 관리자 -> 입력한 데이터로 상영시간표 수정 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_TIMETABLE_CHANGE + "`" + selectedCustom.getTimeTable().getId() + "`" + selectedScreen.getId() + "`" + selectedMovie.getId() + "`" + date + start_time + "`" + date + end_time);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_TIMETABLE_CHANGE))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							initList();
							mainGUI.alert("수정 성공", "상영시간표 수정 성공");
							break;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("수정 실패", "상영시간표 중복 발생");
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("수정 실패", "서버에서 DB접속 오류");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "상영시간표 수정 실패");
			e.printStackTrace();
		}
	}
	
	// 상영시간표 제거
	@FXML
	void remove(ActionEvent event)
	{
		try
		{
			if (selectedCustom == null)
			{
				mainGUI.alert("경고", "데이터를 선택해주세요");
				return;
			}
			
			ButtonType type = mainGUI.confirm("확인", "정말로 삭제하시겠습니까?");
			if (type != ButtonType.OK)
			{
				return;
			}
			
			String timetable_id = selectedCustom.getTimeTable().getId();
			
			// 관리자 -> 선택한 상영시간표 삭제 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_TIMETABLE_DELETE + "`" + timetable_id);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_TIMETALBE_DELETE))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							initList();
							mainGUI.alert("삭제 성공", "상영시간표 삭제 성공");
							break;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("삭제  실패", "상영시간표 삭제 실패");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			mainGUI.alert("오류", "상영시간표 삭제 실패");
			e.printStackTrace();
		}
	}
	
	// 텍스트 필드 클릭시 리스트뷰 보이게하기
	@FXML
	void clickedTfMovie(MouseEvent event)
	{
		lv_movie.getItems().clear();
		lv_movie.getItems().addAll(movie_list);
		lv_movie.setMaxHeight(130);
	}
	
	// 선택한 영화관에 맞는 상영관 불러오기
	@FXML
	void clickedTfScreen(MouseEvent event)
	{
		try
		{
			if (selectedThea == null)
			{
				mainGUI.alert("오류", "영화관을 선택하세요.");
				return;
			}
			
			// 관리자 -> 선택한 영화관에 존재하는 상영관 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_SCREEN_VIEW + "`" + selectedThea.getId());
			screen_list = FXCollections.observableArrayList();
			
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
						case "1": // 요청 성공 시 상영관 리스트에 추가
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
							lv_screen.setItems(FXCollections.observableArrayList());
							lv_screen.getItems().addAll(screen_list);
							lv_screen.setOnMouseClicked((MouseEvent) ->
							{
								if (lv_screen.getSelectionModel().getSelectedItem() == null)
									return;
								selectedScreen = lv_screen.getSelectionModel().getSelectedItem();
								lv_screen.setMaxHeight(0);
								lv_screen.getItems().clear();
								tf_screen.setText(selectedScreen.getName());
							});
							lv_screen.setCellFactory(lv -> new ListCell<ScreenDTO>()
							{
								@Override
								protected void updateItem(ScreenDTO item, boolean empty)
								{
									super.updateItem(item, empty);
									setText(item == null ? null : item.getName());
								}
							});
							
							// 필드에 값이 없다면 리스트뷰 감추기
							if (screen_list.size() == 0)
							{
								lv_screen.getItems().clear();
								lv_screen.setMaxHeight(0);
								return;
							}
							lv_screen.setMaxHeight(130);
							// 값이 있으면 리스트뷰 활성화
							break;
						}
						case "2": // 해당 영화관에 상영관 존재하지 않음
						{
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "상영관 리스트 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void clickedTfTheater(MouseEvent event)
	{
		lv_theater.getItems().clear();
		lv_theater.getItems().addAll(theater_list);
		lv_theater.setMaxHeight(130);
	}
	
	// 외부 클릭시 리스트뷰 보이지 않게 하기
	@FXML
	void clickedParent(MouseEvent event)
	{
		lv_theater.setMaxHeight(0);
		lv_theater.getItems().clear();
		lv_screen.setMaxHeight(0);
		lv_screen.getItems().clear();
		lv_movie.setMaxHeight(0);
		lv_movie.getItems().clear();
	}
	
	// 텍스트필드에 타이핑시 발생하는 이벤트
	@FXML
	void typedTfMovie(KeyEvent event)
	{
		// 필드에 값이 없다면 리스트뷰 감추기
		if (tf_movie.getText().equals(""))
		{
			selectedMovie = null;
			lv_movie.getItems().clear();
			lv_movie.setMaxHeight(0);
			return;
		}
		
		// 값이 있으면 리스트뷰 활성화
		lv_movie.setMaxHeight(130);
		lv_movie.setItems(FXCollections.observableArrayList());
		ObservableList<MovieDTO> temp_list = FXCollections.observableArrayList();
		for (int i = 0; i < movie_list.size(); i++)
		{
			// 모든 사용자 리스트에서 현재 입력한 값을 포함하는 요소가 있는지 확인
			if (movie_list.get(i).getTitle().contains(tf_movie.getText()))
			{
				temp_list.add(movie_list.get(i));
			}
		}
		lv_movie.getItems().addAll(temp_list);
	}
	
	// 상영시간표 검색
	@FXML
	void searchTimeTable(ActionEvent event)
	{
		initList();
	}
	
	// 입력 필드 초기화
	@FXML
	void initBtn(ActionEvent event)
	{
		AdminMain.loadPage("movie_table_manage");
	}
	
	// 리스트 초기화
	private void initList()
	{
		try
		{
			custom_list.clear();
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			String mov_id = selectedMovie == null ? "%" : selectedMovie.getId();
			String screen_id = selectedScreen == null ? "%" : selectedScreen.getId();
			String date = dp_start_date.getValue() == null ? "1976-01-01 " : dateFormat.format(dp_start_date.getValue()) + " ";
			String start_time = mb_hours_start.getText().equals("시간") ? "00:00:00.0" : (mb_minute_start.getText().equals("분") ? mb_hours_start.getText().replace("시", "") + ":00:00.0" : mb_hours_start.getText().replace("시", "") + ":" + mb_minute_start.getText().replace("분", "") + ":00.0");
			String end_time = mb_hours_end.getText().equals("시간") ? "23:59:00.0" : (mb_minute_end.getText().equals("분") ? mb_hours_end.getText().replace("시", "") + ":00:00.0" : mb_hours_end.getText().replace("시", "") + ":" + mb_minute_end.getText().replace("분", "") + ":00.0");
			String theater_id = "null";
			
			// 관리자 -> 선택 영화에 해당하는 상영시간표 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_TIMETABLE_VIEW + "`" + mov_id + "`" + screen_id + "`" + date + "`" + start_time + "`" + end_time + "`" + theater_id);
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_TIMETABLE_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							String screenList = packetArr[3];
							String listArr[] = screenList.split("\\{"); // 각 상영시간표 리스트 분할
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 상영시간표 별 정보 분할
								String tb_id = infoArr[0];
								String tb_screen_id = infoArr[1];
								String tb_mov_id = infoArr[2];
								String tb_type = infoArr[3];
								String tb_current_rsv = infoArr[4];
								String tb_start_time = infoArr[5];
								String tb_end_time = infoArr[6];
								
								custom_list.add(new CustomDTO(new TimeTableDTO(tb_id, tb_mov_id, tb_screen_id, tb_start_time, tb_end_time, tb_type, Integer.valueOf(tb_current_rsv))));
							}
							break;
						}
						case "2": // 상영시간표 리스트 비어있음
						{
							break;
						}
						case "3": // 요청 실패
						{
							mainGUI.alert("오류", "상영시간표 요청 실패했습니다.");
							break;
						}
					}
					if (result != null)
						return;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// DTO들을 모은 DTO
	private class CustomDTO
	{
		TheaterDTO theater;
		ScreenDTO screen;
		MovieDTO movie;
		TimeTableDTO timetable;
		
		public CustomDTO(TimeTableDTO timetable) throws Exception
		{
			try
			{
				// 상영시간표에 해당하는 영화관, 상영관, 영화 정보 요청
				mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_CUSTOM_INFO + "`1`" + timetable.getId());
				
				while (true)
				{
					String packet = mainGUI.readLine(); // 요청 응답 수신
					String packetArr[] = packet.split("`"); // 패킷 분할
					String packetType = packetArr[0];
					String packetCode = packetArr[1];
					
					if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_CUSTOM_INFO))
					{
						String result = packetArr[2];
						
						switch (result)
						{
							case "1": // 요청 성공
							{
								this.timetable = timetable;
								String infoList = packetArr[3];
								String listArr[] = infoList.split("\\{"); // 각 리스트 분할
								String sc_info[] = listArr[0].split("\\|"); // 상영관 정보 분할
								String mv_info[] = listArr[1].split("\\|"); // 영화 정보 분할
								String th_info[] = listArr[2].split("\\|"); // 영화관 정보 분할
								
								screen = new ScreenDTO(sc_info[0], sc_info[1], sc_info[2], Integer.valueOf(sc_info[3]), Integer.valueOf(sc_info[4]), Integer.valueOf(sc_info[5]));
								movie = new MovieDTO(mv_info[0], mv_info[1], mv_info[2], mv_info[3], mv_info[4], mv_info[5], mv_info[6], mv_info[7], mv_info[8], mv_info[9], Integer.valueOf(mv_info[10]));
								theater = new TheaterDTO(th_info[0], th_info[1], th_info[2], Integer.valueOf(th_info[3]), Integer.valueOf(th_info[4]));
								return;
							}
							case "2": // 요청 실패
							{
								mainGUI.alert("경고", "정보 요청 실패했습니다.");
								return;
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public StringProperty getMovie()
		{
			return new SimpleStringProperty(movie.getTitle());
		}
		
		public StringProperty getTheater()
		{
			return new SimpleStringProperty(theater.getName());
		}
		
		public StringProperty getScreen()
		{
			return new SimpleStringProperty(screen.getName());
		}
		
		public StringProperty getStartTime()
		{
			return new SimpleStringProperty(timetable.getStartTime().toString());
		}
		
		public StringProperty getEndTime()
		{
			return new SimpleStringProperty(timetable.getEndTime().toString());
		}
		
		public StringProperty getCurrent()
		{
			return new SimpleStringProperty(timetable.getCurrentRsv() + "/" + screen.getTotalCapacity());
		}
		
		public TimeTableDTO getTimeTable()
		{
			return timetable;
		}
	}
}
