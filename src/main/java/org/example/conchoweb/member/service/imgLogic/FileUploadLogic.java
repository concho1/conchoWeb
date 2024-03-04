package org.example.conchoweb.member.service.imgLogic;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.example.conchoweb.DriveQuickstart;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class FileUploadLogic {
    private final MemberDAO memberDAO;

    private static String UPLOADED_FOLDER = "/uploads/"; // 저장할 위치
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    //사용자의 토큰을 어디에 저장할지 경로를 지정
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    //어플리케이션이 요청하는 권한의 범위를 지정
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    //비밀키 경로
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public FileUploadLogic(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException{
        //credentials.json 파일을 in에 저장함
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {   // credentials이 빈값이면
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8070).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }

    public String tryMakeFolder(String memberEmail) throws GeneralSecurityException, IOException{
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String folderId = "";
        try {
            // 폴더의 존재 여부를 확인합니다.
            FileList result = service.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and name='" + memberEmail + "' and trashed=false")
                    .setSpaces("drive")
                    .setFields("files(id, name)")
                    .execute();

            List<File> files = result.getFiles();
            if (files.isEmpty()) {
                // 폴더가 없으면 생성합니다.
                File fileMetadata = new File();
                fileMetadata.setName(memberEmail);
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                File folder = service.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                folderId = folder.getId();
                System.out.println("Folder ID: " + folderId);
            } else {
                // 폴더가 이미 존재하는 경우, 해당 폴더의 ID를 사용합니다.
                folderId = files.get(0).getId();
                System.out.println("Folder ID: " + folderId);
            }
        }catch (Exception e){

        }
        return folderId;
    }

    // 파일 업로드 ===============================================================================
    public boolean tryUpload(MultipartFile multipartFile, String memberEmail) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();


        String folderId = "";

        Optional<MemberDTO> memberDTO =  memberDAO.findUserByEmail(memberEmail);
        if(memberDTO.isPresent()){
            MemberDTO member = memberDTO.get();
            folderId = member.getFolderId();
        }


        try {
            // Drive 파일 메타데이터를 생성합니다. 여기서 파일 이름과 부모 폴더를 설정합니다.
            File fileMetadata = new File();
            fileMetadata.setName(multipartFile.getOriginalFilename());
            // 파일을 특정 폴더 안에 업로드하기 위해 부모 폴더의 ID를 설정합니다.
            if (!folderId.isEmpty()) {
                fileMetadata.setParents(Collections.singletonList(folderId));
            }

            ByteArrayContent fileContent = new ByteArrayContent(multipartFile.getContentType(), multipartFile.getBytes());

            // Google Drive에 파일을 생성(업로드)하는 API 호출을 실행합니다.
            File file = service.files().create(fileMetadata, fileContent)
                    .setFields("id, name")
                    .execute();

            // 업로드 성공 메시지와 파일 정보를 출력합니다.
            System.out.printf("File ID: %s Name: %s uploaded %s successfully\n", file.getId(), file.getName(), multipartFile.getName());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
