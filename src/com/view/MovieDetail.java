package com.view;

import java.util.ArrayList;

import com.db.model.DTO;
import com.db.model.MemberDTO;
import com.db.model.MovieDTO;
import com.db.model.ReviewDTO;
import com.main.mainGUI;
import com.protocol.Protocol;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.Callback;

public class MovieDetail
{
	private MovieDTO movie;
	
	private ObservableList<CustomDTO> custom_list;
	
	@FXML
	private Button btn_reservation;
	
	@FXML
	private ImageView image_movie;
	
	@FXML
	private Text text_title;
	
	@FXML
	private Text text_open_date;
	
	@FXML
	private Text text_director;
	
	@FXML
	private Text text_actor;
	
	@FXML
	private TableView<CustomDTO> tv_review;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_reviewer;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_review_score;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_review_date;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_review;
	
	@FXML
	private TableColumn<CustomDTO, String> tc_other;
	
	@FXML
	private MenuButton mb_review;
	
	@FXML
	private TextField tf_review;
	
	@FXML
	private Button btn_review;
	
	@FXML
	private WebView web_trailer;
	
	static private WebView webview;
	
	@FXML
	private ImageView image_stillcut;
	
	@FXML
	private ImageView image_stillcut2;
	
	@FXML
	private ImageView image_stillcut3;
	
	@FXML
	private Text text_plot;
	
	// 영화 상제 정보 초기화
	public void initData(MovieDTO movie) 
	{
		try
		{
			this.movie = movie;
			webview = web_trailer; // 백그라운드로 돌아가는 웹뷰 종료를 위해서 값 복사
			
			if (movie.getIsCurrent().equals("2"))
			{
				mb_review.setDisable(true);
				tf_review.setDisable(true);
				btn_review.setDisable(true);
				tv_review.setDisable(true);
			}
			
			// 스틸컷 url 분리
			String stillcut[] = movie.getStillCutPath().split(" ");
			ImageView view_arr[] = {
					image_stillcut, image_stillcut2, image_stillcut3 };
			
			text_title.setText(movie.getTitle());
			text_open_date.setText(movie.getReleaseDate().toString());
			text_director.setText(movie.getDirector());
			text_actor.setText(movie.getActor());
			text_plot.wrappingWidthProperty().set(225);
			text_plot.setText(movie.getPlot().replace("}", "\n"));
			Image image = new Image(movie.getPosterPath());
			image_movie.setImage(image);
			for (int i = 0; i < view_arr.length; i++)
			{
				Image image_temp = new Image(stillcut[i]);
				view_arr[i].setImage(image_temp);
			}
			String[] trailer = movie.getTrailerPath().split("v=");
			try
			{
				web_trailer.getEngine().load("http://www.youtube.com/embed/" + trailer[1] + "?autoplay=0");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			custom_list = FXCollections.observableArrayList();
			
			initList();
			
			tv_review.getItems().clear();
			
			tc_reviewer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMember().getId()));
			tc_review_score.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getReview().getStar())));
			tc_review_date.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReview().getWriteTime().toString()));
			tc_review.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReview().getText()));
			// 테이블 뷰에 버튼을 넣기위한 필사의 노력
			tc_other.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomDTO, String>, ObservableValue<String>>()
			{
				@Override
				public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomDTO, String> p)
				{
					return new SimpleStringProperty(p.getValue().getMember().getId());
				}
			});
			tc_other.setCellFactory(cellData -> new ButtonCell());
			
			tv_review.setItems(custom_list);
			
			// 별점 세팅
			for (int i = 1; i <= 10; i++)
			{
				MenuItem score = new MenuItem(Integer.toString(i));
				score.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						mb_review.setText(score.getText());
					}
				});
				mb_review.getItems().add(score);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@FXML
	void addReview(ActionEvent event) // 리뷰 추가
	{
		try
		{
			if (mb_review.getText().equals("평점") || tf_review.getText().equals(""))
			{
				mainGUI.alert("에러", "평점과 리뷰를 입력해주세요");
			}
			
			// 리뷰 등록 요청
			mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_REVIEW_ADD + "`" + DTO.EMPTY_ID + "`" + Login.USER_ID + "`" + movie.getId() + "`" + Integer.valueOf(mb_review.getText()) + "`" + tf_review.getText() + "`" + "2000-01-01 00:00:00.0");
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 리뷰 등록 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_REVIEW_ADD))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공
						{
							initList();
							tf_review.clear();
							mb_review.setText("평점");
							mainGUI.alert("등록 성공", "리뷰 등록 성공");
							return;
						}
						case "2": // 요청 실패
						{
							mainGUI.alert("등록 실패", "리뷰 등록 실패");
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
	
	@FXML
	void getReservation(ActionEvent event) // 예매화면으로 이동 - 해당 영화 자동 선택됨
	{
		try
		{
			stopWebview();
			FXMLLoader loader = new FXMLLoader(MovieTable.class.getResource("./xml/user_sub_page/movie_table.fxml"));
			Parent root = loader.load();
			MovieTable controller = loader.<MovieTable>getController();
			controller.initData(movie);
			
			UserMain.user_sub_root.setCenter(root);
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
			custom_list.clear();
			
			// 리뷰 리스트 요청
			mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_REVIEW_VIEW + "`" + movie.getId());
			
			ArrayList<ReviewDTO> r_list = new ArrayList<ReviewDTO>();
			
			while (true)
			{
				String packet = mainGUI.readLine(); // 리뷰 리스트 요청 응답 수신
				String packetArr[] = packet.split("`"); // 패킷 분할
				String packetType = packetArr[0];
				String packetCode = packetArr[1];
				
				if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_REVIEW_VIEW))
				{
					String result = packetArr[2]; // 요청 결과
					
					switch (result)
					{
						case "1": // 요청 성공 시 데이터 추가
						{
							String reviewList = packetArr[3];
							String listArr[] = reviewList.split("\\{"); // 각 리뷰 분리
							
							for (String listInfo : listArr)
							{
								String infoArr[] = listInfo.split("\\|"); // 리뷰 별 정보 분리
								String rv_id = infoArr[0];
								String rv_memId = infoArr[1];
								String rv_movId = infoArr[2];
								int rv_star = Integer.parseInt(infoArr[3]);
								String rv_text = infoArr[4];
								String rv_time = infoArr[5];
								
								r_list.add(new ReviewDTO(rv_id, rv_memId, rv_movId, rv_star, rv_text, rv_time));
							}
							for (ReviewDTO temp : r_list) // 리스트에 추가
							{
								custom_list.add(new CustomDTO(temp));
							}
							return;
						}
						case "2": // 요청 실패
						{
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
	
	private class CustomDTO
	{
		private MemberDTO member;
		private ReviewDTO review;
		
		public CustomDTO(ReviewDTO review)
		{
			try
			{
				// 리뷰 등록을 위한 사용자 정보 요청
				mainGUI.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.CS_REQ_CUSTOM_INFO + "`3`" + review.getMemberId());
				
				while (true)
				{
					String packet = mainGUI.readLine(); // 정보 요청 응답 수신
					String packetArr[] = packet.split("`"); // 패킷 분할
					String packetType = packetArr[0];
					String packetCode = packetArr[1];
					
					if (packetType.equals(Protocol.PT_RES_VIEW) && packetCode.equals(Protocol.SC_RES_CUSTOM_INFO))
					{
						String result = packetArr[2]; // 요청 결과
						
						switch (result)
						{
							case "1": // 요청 성공
							{
								this.review = review;
								String infoList = packetArr[3];
								String mem_info[] = infoList.split("\\|"); // 회원 정보 분할
								member = new MemberDTO(mem_info[0], mem_info[1], mem_info[2], mem_info[3], mem_info[4], mem_info[5], mem_info[6], mem_info[7]);
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
		
		public MemberDTO getMember()
		{
			return member;
		}
		
		public ReviewDTO getReview()
		{
			return review;
		}
	}
	
	private class ButtonCell extends TableCell<CustomDTO, String>
	{
		final Button cellButton = new Button("삭제");
		
		ButtonCell()
		{
			cellButton.setOnAction((t) ->
			{
				try
				{
					CustomDTO currentCustom = getTableView().getItems().get(getIndex());
					
					// 리뷰 삭제 요청
					mainGUI.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.CS_REQ_REVIEW_DELETE + "`" + currentCustom.getReview().getId());
					while (true)
					{
						String packet = mainGUI.readLine(); // 요청 응답 수신
						String packetArr[] = packet.split("`"); // 패킷 분할
						String packetType = packetArr[0];
						String packetCode = packetArr[1];
						
						if (packetType.equals(Protocol.PT_RES_RENEWAL) && packetCode.equals(Protocol.SC_RES_REVIEW_DELETE))
						{
							String result = packetArr[2];
							
							switch (result)
							{
								case "1": // 요청 성공 시 리스트 초기화
								{
									initList();
									mainGUI.alert("삭제 성공", "리뷰 삭제 성공");
									return;
								}
								case "2": // 요청 실패
								{
									mainGUI.alert("삭제 실패", "리뷰 삭제 실패");
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
				
			});
			
		}
		
		@Override
		protected void updateItem(String item, boolean empty)
		{
			super.updateItem(item, empty);
			if (!empty)
			{
				if (Login.USER_ID.equals(item))
				{
					setGraphic(cellButton);
				}
				else
				{
					setGraphic(null);
				}
			}
			else
			{
				setGraphic(null);
			}
		}
	}
	
	static public void stopWebview()
	{
		if (webview != null)
			webview.getEngine().load(null);
	}
	
}
