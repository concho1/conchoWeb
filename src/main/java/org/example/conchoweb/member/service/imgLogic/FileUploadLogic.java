package org.example.conchoweb.member.service.imgLogic;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class FileUploadLogic {
    private final MemberDAO memberDAO;
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public FileUploadLogic(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    // 싱글톤 패턴 메서드 이용
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        return FileLogic.getCredential();
    }

    // 회원가입시 폴더 만드는 메서드
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
