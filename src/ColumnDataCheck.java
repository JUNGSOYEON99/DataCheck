import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// 각 file별 column_data.csv에 정의된 컬럼명 및 갯수가 실제 물리 파일과 일치하는가? 순서도 일치하는가?
// 컬럼명에 공백이 있는지도 함께 체크
public class ColumnDataCheck extends DataCheckAll{
	//1. column_data.csv파일 내용 읽어와서 분류하기 (file_id, name)
	public static HashMap<String,ArrayList<String>> ReadColumnData(String fileUrl) {
		HashMap<String, ArrayList<String>> file_column_data = new HashMap<String, ArrayList<String>>();
		List<List<String>> csvList = new ArrayList<List<String>>();
		List<String> FileId = new ArrayList<String>();
		File csv = new File(fileUrl + "\\column_data.csv");
		//File csv = new File(fileUrl + "column_data.csv");
        BufferedReader br = null;
        String line = "";
        int id_num = 0;
        
        try {
            br = new BufferedReader(new FileReader(csv));
            // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
            while ((line = br.readLine()) != null) { 
                List<String> aLine = new ArrayList<String>();
                // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
                String[] lineArr = line.split(","); 
                aLine = Arrays.asList(lineArr);
                csvList.add(aLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) { 
                	// 사용 후 BufferedReader를 닫아준다.
                    br.close(); 
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        int csvList_size = csvList.size();
        for (int j = 0; j < csvList_size; j++) {
			for (int a = 0; a < csvList.get(j).size(); a++) {
    			if(csvList.get(j).get(a).equals("file_id")) {
    				id_num = a;
    				break;
    			}
			}
		}
        // columnData.csv 파일에서 파일id 가져와서 리스트에 저장
        for(int i = 0; i < csvList_size; i++) {
        	if(csvList.get(i) != null) {
        		if(!csvList.get(i).get(id_num).contains("file_id")) {
        			FileId.add(csvList.get(i).get(id_num));
        		}
        	} 
		}
        //중복값 제거하기 위해
        Set<String> set = new HashSet<String>(FileId);
        // Set을 List로 변경
    	List<String> newList =new ArrayList<String>(set);
    	//list 정렬
    	Collections.sort(newList);
    	int newList_size = newList.size();
    	
    	for(int i = 0; i < csvList_size; i++) {
    		if(csvList.get(i) != null && !csvList.get(i).get(id_num).contains("id")) {
    			for(int j = 0; j < newList_size; j++) {
    				if(newList.get(j).equals(csvList.get(i).get(2))) {
    					String file_id = newList.get(j);
    					ArrayList<String> valueList = new ArrayList<String>();
    					for(int z = 0; z < csvList_size; z++) {
    						 if(file_id.equals(csvList.get(z).get(2))) {
    							 //valueList에 값을 추가한다.
    							 // UTF-8 BOM 파일을 읽어올 때 파일 내용의 맨 앞에 특정 문자가 추가된다.
    							 // replace()함수를 이용하여 특정 문자를 제거한다.
    						 	valueList.add(csvList.get(z).get(3).replace("\uFEFF", "")); 
    						 }
    					}
    					file_column_data.put(file_id, valueList);
    				}
    			}
        	}
    	}
        return file_column_data;
	}		
	//2. csv파일에서 읽어온 file_id를 이용해서 폴더에 저장된 해당 파일의 컬럼명 읽어오기
	public static HashMap<String, ArrayList<String>> readFileColumn(String folderUrl) {
		HashMap<String, ArrayList<String>> folder_file_name = new HashMap<String, ArrayList<String>>();
		
		List<String> AllFiles = readFileName(folderUrl);
		
		for(int i = 0; i < AllFiles.size(); i++) {
			List<List<String>> resultList = new ArrayList<List<String>>();
			if(AllFiles.get(i) != "") {
				String fileName = folderUrl +"\\"+ AllFiles.get(i);
				//String fileName = folderUrl + AllFiles.get(i);
				BufferedReader br = null;
		        String line = "";

		        try {
		            br = new BufferedReader(new FileReader(fileName));
		            while ((line = br.readLine()) != null) { 
		                List<String> aLine = new ArrayList<String>();
		                String[] lineArr = line.split(","); 
		                aLine = Arrays.asList(lineArr);
		                resultList.add(aLine);
		                break;
		            }
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                if (br != null) {
		                    br.close(); 
		                }
		            } catch(IOException e) {
		                e.printStackTrace();
		            }
		        }
		        int resultList_size = resultList.size();
		        for(int x = 0; x < resultList_size; x++) {
		        	ArrayList<String> valueList = new ArrayList<String>();
		        	for(int y = 0; y < resultList.get(0).size(); y++) {
		        		valueList.add(resultList.get(0).get(y).replace("\uFEFF", "")); 
		        	}
		        	folder_file_name.put(AllFiles.get(i),valueList);
		    	}
			} 
		}
		return folder_file_name;
	}
	
	//1과 2를 비교해서 컬럼명, 갯수, 순서가 일치하는지 체크 (컬럼명 공백 체크)
	public ColumnDataCheck(String folderUrl) throws IOException{
		// TODO Auto-generated method stub		

		int ch = 0;
		// 한글이 깨지는 것을 방지하기 위해 filewriter 사용 x
		HashMap<String, ArrayList<String>> file_column_data = ReadColumnData(folderUrl);
		// 폴더에 존재하는 파일들 파일명과 각 파일의 컬럼명 저장
		HashMap<String, ArrayList<String>> folder_file_name = readFileColumn(folderUrl);
		// csv 파일의 파일id, id별 컬럼명 저장한 hashmap에서 키 값만 가져옴
		Set<String>file_column_set = file_column_data.keySet();
		// 폴더에 존재하는 파일들 파일명과 각 파일의 컬럼명 저장한 hashmap에서 키 값만 가져옴
		Set<String>folder_file_set = folder_file_name.keySet();
		// Iterator를 활용해서 list의 모든 값을 가져온다.
		Iterator<String>file_column_it = file_column_set.iterator();
		Iterator<String>folder_file_it = folder_file_set.iterator();
		// 각 hashmap의 키를 순서로 저장하기 위한 list
		List<String> file_column_key = new ArrayList<String>();
		List<String> folder_file_key = new ArrayList<String>();
		// hasnext() 함수를 통해서 항목이 있는지 확인을 하고 
		while(file_column_it.hasNext()) {
			// next() 함수를 통해서 요소를 하나씩 하나씩 갖고와서 List에 저장
			file_column_key.add(file_column_it.next());
		}
		while(folder_file_it.hasNext()) {
			folder_file_key.add(folder_file_it.next());
		}
		// csv 파일의 파일id, id별 컬럼명 저장
		// 컬럼명, 갯수, 순서가 일치하는지 체크 ( 컬럼명 공백 확인 )
		int file_column_key_size = file_column_key.size();
		int folder_file_key_size = folder_file_key.size();
		
		for(int i = 0; i < file_column_key_size; i++) {
			for(int j = 0; j < folder_file_key_size; j++) {
				if(folder_file_key.get(j).contains(file_column_key.get(i))) {
					// 컬럼명 비교 , 순서 및 컬럼 공백 체크
					String keyName = folder_file_key.get(j);
		        	int idx = keyName.lastIndexOf(".");
		        	String file_keyName = keyName.substring(0,idx);
					// 컬럼 순서 체크하기 위한 변수
					int check = 0;
					if(file_column_key.get(i).equals(file_keyName) && folder_file_key.get(j).equals(keyName)) {
						// 두 파일명에 대한 컬럼수가 일치할 때 
						if(file_column_data.get(file_keyName).size() == folder_file_name.get(keyName).size()) {
							for(int y = 0; y < file_column_data.get(file_keyName).size(); y++) {
				        		if(!file_column_data.get(file_keyName).get(y).trim().toLowerCase().equals(folder_file_name.get(keyName).get(y).trim().toLowerCase())) {
				        			System.out.println(keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명이 일치하지 않습니다!");
				        			System.out.println("column_data.csv 컬럼명 : " + file_column_data.get(file_keyName).get(y) + " / 폴더 파일 컬럼명 : " + folder_file_name.get(keyName).get(y));

				        			check++;
				        			ch++;
				        		}
				        		if(file_column_data.get(file_keyName).get(y).contains(" ")) {
				        			System.out.println("csv파일 안에 " + keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명에 공백이 존재합니다!");
				        			
				        			ch++;
				        		}
				        		if(folder_file_name.get(keyName).get(y).contains(" ")) {
				        			System.out.println("폴더 안의 " + keyName + " 파일의 " + file_column_data.get(keyName).get(y) + "컬럼명에 공백이 존재합니다!");

				        			ch++;
				        		}
				        	}
							// 컬럼 순서 확인
							if(check > 0){
			        			System.out.println(keyName + " 파일의 컬럼 순서가 일치하지 않습니다!");
			        			System.out.println("");
			        			ch++;
			        		} else if(check == 0) {
			        			//System.out.println(keyName + " 파일의 컬럼명과 컬럼 순서가 일치합니다!");
			        		}
							// 갯수 확인
							if(file_column_data.get(file_keyName).size() != folder_file_name.get(keyName).size()) {
								System.out.println(keyName + " 파일의 컬럼 갯수가 일치하지 않습니다!");
								System.out.println("");
								ch++;
							} else {
								//System.out.println(keyName + " 파일의 컬럼 갯수가 일치합니다!");
								//System.out.println("");
							}
						}
						// column_data.csv 컬럼 수 > 폴더안의 파일의 컬럼 수
						else if (file_column_data.get(file_keyName).size() > folder_file_name.get(keyName).size()) {
							for(int y = 0; y < folder_file_name.get(keyName).size(); y++) {
								if(!file_column_data.get(file_keyName).get(y).trim().toLowerCase().equals(folder_file_name.get(keyName).get(y).trim().toLowerCase())) {
				        			System.out.println(keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + " 컬럼명이 일치하지 않습니다!");
				        			System.out.println("column_data.csv 컬럼명 : " + file_column_data.get(file_keyName).get(y) + "  / 폴더 파일 컬럼명 : " + folder_file_name.get(keyName).get(y));
				        			
				        			check++;
				        			ch++;
								} else if(file_column_data.get(file_keyName).get(y) != null &&
									  folder_file_name.get(keyName).get(y) == null) {
									  System.out.println("column_data.csv 컬럼"); 
									}
									 
				        		if(file_column_data.get(file_keyName).get(y).contains(" ")) {
				        			System.out.println("csv파일 안에 " + keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명에 공백이 존재합니다!");
				        			
				        			ch++;
				        		}
				        		if(folder_file_name.get(keyName).get(y).contains(" ")) {
				        			System.out.println("폴더 안의 " + keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명에 공백이 존재합니다!");
				        			
				        			ch++;
				        		}
				        		if(y == folder_file_name.get(keyName).size() - 1) {
				        			System.out.println( "column_data.csv 파일에 있는 " + keyName + "의 컬럼의 수가 더 많습니다.");
				        			
				        			String columnName = "";
				        			for(int a = 1; a <= file_column_data.get(file_keyName).size() - folder_file_name.get(keyName).size(); a++) {
				        				if(a == 1) {
				        					columnName += file_column_data.get(file_keyName).get(y + a);
				        				} else {
				        					columnName = columnName + ", " + file_column_data.get(file_keyName).get(y + a);
				        				}
				        			}
				        			System.out.println( "column_data.csv 파일에만 있는 " + keyName + "의 컬럼 : " + columnName);
				        			
				        			ch++;
				        		}
				        	}
							// 컬럼 순서 확인
							if(check > 0){
			        			System.out.println(keyName + " 파일의 컬럼 순서가 일치하지 않습니다!");
			        			System.out.println("");
			        			ch++;
			        		} else if(check == 0) {
			        			System.out.println(keyName + " 파일의 그 외 컬럼명과 컬럼 순서가 일치합니다!");
			        			System.out.println("");
			        		}	
						}
						// column_data.csv 컬럼 수 < 폴더안의 파일의 컬럼 수
						else if (file_column_data.get(file_keyName).size() < folder_file_name.get(keyName).size()) {
							for(int y = 0; y < file_column_data.get(file_keyName).size(); y++) {
								if(!file_column_data.get(file_keyName).get(y).trim().toLowerCase().equals(folder_file_name.get(keyName).get(y).trim().toLowerCase())) {
				        			System.out.println(keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명이 일치하지 않습니다!");
				        			System.out.println("column_data.csv 컬럼명: " + file_column_data.get(file_keyName).get(y) + "  / 폴더 파일 컬럼명 " + folder_file_name.get(keyName).get(y));
				        		
				        			check++;
				        			ch++;
								} else if(file_column_data.get(file_keyName).get(y) != null &&
									  folder_file_name.get(keyName).get(y) == null) {
									  System.out.println("column_data.csv 컬럼"); 
								} 
				        		if(file_column_data.get(file_keyName).get(y).contains(" ")) {
				        			System.out.println("csv파일 안에 " + keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명에 공백이 존재합니다!");
				        			
				        			ch++;
				        		}
				        		if(folder_file_name.get(keyName).get(y).contains(" ")) {
				        			System.out.println("폴더 안의 " + keyName + " 파일의 " + file_column_data.get(file_keyName).get(y) + "컬럼명에 공백이 존재합니다!");
				        			
				        			ch++;
				        		}
				        		if(y == file_column_data.get(file_keyName).size() - 1) {
				        			System.out.println( "폴더 안의 " + keyName + " 파일의 컬럼의 수가 더 많습니다.");
				        			
				        			String columnName = "";
				        			for(int a = 1; a <= folder_file_name.get(keyName).size() - file_column_data.get(file_keyName).size(); a++) {
				        				if(a == 1) {
				        					columnName += folder_file_name.get(keyName).get(y + a);
				        				} else {
				        					columnName = columnName + ", " + folder_file_name.get(keyName).get(y + a);
				        				}
				        			}
				        			System.out.println( "폴더 안의 " + keyName + " 파일에만 있는 컬럼 : "+columnName);
				        			
				        			ch++;
				        		}
				        	}
							// 컬럼 순서 확인
							if(check > 0){
			        			System.out.println(keyName + " 파일의 컬럼 순서가 일치하지 않습니다!");
			        			System.out.println("");
			        			ch++;
			        		} else if(check == 0) {
			        			System.out.println(keyName + " 파일의 그 외 컬럼명과 컬럼 순서가 일치합니다!");
			        			System.out.println("");
			        		}
						}
					}
				}
			}
		}
		
		if(ch == 0) {
			System.out.println("오류 내용이 없습니다.");
			
		}
		System.out.println("ColumnData 검사 완료!");
		System.out.println("==============================================================================");
		System.out.println("\n");
	}
}
