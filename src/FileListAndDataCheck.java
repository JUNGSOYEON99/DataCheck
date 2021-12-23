import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//file_data.csv에 있는 파일 목록과 맞게 실재 물리적 파일 데이터가 들어왔는가?
public class FileListAndDataCheck extends DataCheckAll{
	
	//4번 검증 _ 파일명 확인	
	public FileListAndDataCheck(String folderUrl) throws IOException {
		// file_data.csv의 내용 저장 List
		List<List<String>> FileData = readCSV(folderUrl);
		// file_data.csv의 내용(FileData)에서 dataset_id만 저장하는 List
		List<String> FileId = new ArrayList<String>();
		// file_data.csv의 dataset_id에서 폴더에 저장된 파일 이름이 없는 id들
		List<String> dataset = new ArrayList<String>();
		// file_data.csv의 dataset_id에 폴더에 저장된 파일 이름이 있는 것 
		List<String> result = new ArrayList<String>();
		// 폴더 안의 파일들
		List<String> filesName = readFileName(folderUrl);
		// 폴더에 저장된 파일 이름들에서 file_data.csv의 dataset_id들에 포함되지 않는 것
		List<String> etcFiles = new ArrayList<String>();
		File folder = new File(folderUrl);
		String[] file = folder.list();
		
		int fileLength = file.length;
		
		for(int i = 0; i < fileLength; i++) {
			File f = new File(folderUrl + "\\" + file[i]);
			//File f = new File(folderUrl + file[i]);
			if(f.isDirectory()) {
				file[i] = "";
				
			}
		}
		// 일치하는 데이터 파일 수
		int fileCheck = 0;
		// 폴더에 저장된 파일수
		int folderFileCheck = 0;
		// file_data.csv에 존재하는 dataset_id 수
		int dataIdCheck = 0;
		// file_data.csv에서 'rights(확장자)'가 존재하는 위치를 저장하기 위한 변수
		int rights_num = 0;
		int check = 0;
		int c = 0;
		// file_data.csv에서 'id'가 존재하는 위치를 저장하기 위한 변수
		int id_start_num = 0;
		// csv 파일 내용에서
		int FileDataSize = FileData.size();
		for(int i = 0; i < FileDataSize; i++) {
			if(FileData.get(i) != null) {
				// 'id'가 저장되어 있는 위치를 찾아서
				if(FileData.get(i).get(0).equals("id")) {
					// 변수에 저장
					id_start_num = i;
					break;
				}
			}
		}
		for(int i = 0; i < FileDataSize; i++) {
			if(FileData.get(i) != null) {
				if(FileData.get(0).get(i).equals("rights")) {
					// 변수에 저장
					rights_num = i;
					break;
				}
			}
		}
		// id_start_num은 'id' 컬럼의 위치이기 때문에 +1부터 반복문 시작
		for(int i = id_start_num + 1; i < FileDataSize; i++) {
			// i번째 행이 null 값이 아니면
			if(FileData.get(i) != null && !FileData.get(i).get(0).contains("Column1") && !FileData.get(i).get(0).contains("id")) {
				// FileId 리스트(FileData에서 파일 id만 저장하는 리스트)에 각 파일 id 저장
				FileId.add(FileData.get(i).get(0));
				// file_data.csv에 존재하는 dataset_id 수 추가
				dataIdCheck++;
			}
		}
		int FileIdSize = FileId.size();
		int filesNameSize = filesName.size();
		// file_data.csv의 파일 id와 폴더 안의 파일명과 비교
		for(int i = 0; i < FileIdSize; i++ ) {
			for(int j = 0; j < filesNameSize; j++ ) {
				String file1 = filesName.get(j);
	        	int idx = file1.lastIndexOf(".");
	        	String file1_Name = file1.substring(0,idx);
				// 폴더 안의 파일명 (~.csv)에 file_data.csv에 존재하는 dataset_id가 포함되어 있고, null 값이 아니면
				if(file1_Name.equals(FileId.get(i)) && filesName.get(j) != null && filesName.get(j) != "") {
					// file_data.csv의 dataset_id에 폴더에 저장된 파일 이름이 있는 것을 저장하는 리스트에 추가
					result.add(filesName.get(j));
					// 일치하는 데이터 파일 수 중가
					fileCheck++;
				} 
				// 폴더 안의 파일명이 null 값이 아니면
				if(filesName.get(j) != null && filesName.get(j) != "") {
					// 폴더에 저장된 파일수 증가
					folderFileCheck++;
				}
			}
		}
		// file_data.csv에만 존재하는 파일명을 리스트에 저장하기 위해
		// FileId 리스트 (FileData에서 파일 id만 저장하는 리스트)
		int resultSize = result.size();
		for(int i = 0; i < FileIdSize; i++ ) {
			for(int j = 0; j < resultSize; j++ ) {
				// csv파일의 id와 폴더 안의 파일명이 일치하는 것을 저장해놓은 리스트에서
				// csv 파일 리스트에 존재하는 id랑 일치하는 것이 있다면
				String file1 = result.get(j);
	        	int idx = file1.lastIndexOf(".");
	        	String file1_Name = file1.substring(0,idx);
				if(file1_Name.equals(FileId.get(i))) {
					// 변수 증가
					check++;
				}
			}
			// csv파일의 id와 폴더명이 일치하는 것이 없으면
			if(check == 0) {
				// file_data.csv의 dataset_id 중에서 폴더에 저장된 파일 이름이 없는 id들을 저장하는 리스트 dataset에 추가
				dataset.add(FileId.get(i));
			}
			// 변수 초기화
			check = 0;
		}

		// FileId 리스트 (FileData에서 파일 id만 저장하는 리스트)
		for(int a = 0; a < FileIdSize; a++) {
			for(int i = 0; i < filesNameSize; i++ ) {
				for(int j = 0; j < resultSize; j++ ) {
					// csv파일의 id와 폴더 안의 파일명이 일치하는 것을 저장해놓은 리스트에서
					// 폴더 안에 있는 파일명과 일치하는 것이 있다면
					if(result.get(j).equals(filesName.get(j))) {
						// 변수 증가
						c++;
					}
				}
			}
		}
		//폴더에만 있는 파일 검사
		for(int j = 0; j < fileLength; j++) {
			if(file[j] != "") {
				String file1 = file[j]; 
				int idx = file1.lastIndexOf("."); 
				String file1_Name = file1.substring(0,idx);
				
				if(file[j].contains("column_data") || file[j].contains("datasets") || file[j].contains("file_data") || file[j].contains("keywords")) {
					file[j] = "";
				}
				for(int z = 0; z < FileIdSize; z++) {
					if(file1_Name.equals(FileId.get(z))) {
						file[j] = "";
					}
				}
				if(file[j] != null && file[j] != "") {
					etcFiles.add(file[j]);
				}
			}
		}
		int result_count = fileCheck + etcFiles.size();

		System.out.println("폴더에 저장된 파일 수(메타데이터 제외) : " + result_count + "개");
		System.out.println("file_data.csv에 존재하는 dataset_id 수 : " + dataIdCheck+ "개");
		System.out.println("일치하는 데이터 파일 수 :  " + fileCheck+ "개");
		System.out.println("일치하지 않는 file_data.csv의 dataset_id 수 : " + dataset.size() + "개");
		System.out.println("일치하지 않는 폴더 안의 파일 수 : " + etcFiles.size() + "개");
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("file_data.csv에 있는 파일 목록과 일치하는 폴더 안의 파일들 : " + result.get(0));
		for( int i = 1; i < result.size(); i++) {
			System.out.println("\t\t\t\t\t\t             " + result.get(i));
		}
		System.out.println("");
		if(dataset.size() > 0) {
			System.out.println("일치하지 않는 file_data.csv의 dataset_id : " + dataset.get(0));
			for( int i = 1; i < dataset.size(); i++) {
				System.out.println("\t\t\t\t           " + dataset.get(i));
			}
			System.out.println("");
		}
		if(etcFiles.size() > 0) {
			System.out.println("일치하지 않는 폴더 안의 파일명 : " + etcFiles.get(0));
			for( int i = 1; i < etcFiles.size(); i++) {
				System.out.println("일치하지 않는 폴더 안의 파일명 :  " + etcFiles.get(i));
			}
		}
		System.out.println("");
		System.out.println("FileListAndData 검사 완료!");
		System.out.println("==============================================================================");
		System.out.println("\n");		
	}
}
