/////게시글 작성 관련
const pathArr = [];
//api
tinymce.init({
    license_key: "gpl",
    selector: "#newsContent",
    resize: "both",
    height: 600,
    plugins: "image media emoticons",
    menubar: false,
    statusbar: false,
    promotion: false,
    toolbar:
        "undo redo | fontfamily fontsize bold italic" +
        "forecolor backcolor | alignleft " +
        "aligncenter alignright alignjustify " +
        "| image  media emoticons",
    font_family_formats:
        "Noto Sans KR=Noto Sans KR;" +
        "Noto Serif KR=Noto Serif KR;" +
        "Gowun Batang=Gowun Batang;" +
        "Gowun Dodum=Gowun Dodum;" +
        "Orbit=Orbit;" +
        "Nanum Gothic=Nanum Gothic;" +
        "Nanum Myeongjo=Nanum Myeongjo;" +
        "Nanum Brush Script=Nanum Brush Script;",
    font_size_formats: "12pt 14pt 16pt 18pt 20pt 24pt 28pt 36pt 48pt",
    content_style:
        "@import url('https://fonts.googleapis.com/css2?family" +
        "=Gowun+Batang&family=Gowun+Dodum&family=Nanum+Brush" +
        "+Script&family=Nanum+Gothic&family=Nanum+Myeongjo&f" +
        "amily=Noto+Sans+KR:wght@100..900&family=Noto+Serif+KR:" +
        "wght@200..900&family=Orbit&display=swap');" +
        "body {font-family: 'Noto Sans Kr';",
    file_picker_types: "image",
    file_picker_callback: function (callback, value, meta) {
        const input = document.createElement("input");
        input.setAttribute("type", "file");
        input.setAttribute("accept", "image/*");

        input.onchange = async function () {
            const file = this.files[0];
            const imgName =
                file.name.substring(file.name.lastIndexOf("/") + 1);
            const temppath = await processImage(file, imgName, 1);
            callback(temppath, { title: file.name });
        }
        input.click();
    },
    //이건 기능과 상관 없는 확인용 코드임.
    //무엇을 확인? - 이미지 경로 관련.
    setup: function(editor) {
        console.log('현재 URL:', window.location.pathname);
        console.log('TinyMCE base URL:', editor.documentBaseUrl);
        console.log('TinyMCE settings:', editor.settings);

        editor.on('BeforeSetContent', function(e) {
            console.log('Content before processing:', e.content);
        });

        editor.on('SetContent', function(e) {
            console.log('Content after processing:', e.content);
            // DOM에서 직접 img 태그들을 찾아서 경로 확인
            const images = editor.getBody().getElementsByTagName('img');
            Array.from(images).forEach((img, i) => {
                console.log(`Image ${i + 1}:`, {
                    src: img.getAttribute('src'),
                    'data-mce-src': img.getAttribute('data-mce-src'),
                    baseURI: img.baseURI
                });
            });
        });
    }

});

//썸네일
const thumbBtn = document.getElementById("thumbBtn");
thumbBtn.addEventListener("click", function () {
    const input = document.createElement("input");
    const thumbPre = document.getElementById("thumbPre");
    input.setAttribute("type", "file");
    input.setAttribute("accept", "image/*");

    input.onchange = async function () {
        const file = input.files[0];
        const imgName =
            file.name.substring(file.name.lastIndexOf("/") + 1);
        console.log(imgName);
        const temppath = await processImage(file, imgName, 0);
        thumbPre.src = temppath;
    }
    input.click();
});

//이미지 임시 저장
const processImage = async function(file, imgName, imgType){
    const formData = new FormData();
    formData.append("image", file);
    formData.append("imgName", imgName);
    formData.append("imgType", imgType);

    try {
        const response = await fetch("/image/temp",{
            method:"POST",
            body:formData
        });
        if(!response.ok){
            throw new Error("Upload failed : !response.ok");
        }
        //경로 확인용
        const temppath = await response.text();
        console.log('서버에서 받은 경로:', temppath);
        pathArr.push(temppath);
        console.log('callback에 전달하기 전 경로:', temppath);
        //
        return temppath;
    } catch (error) {
        console.error(error);
    }
};
//////////

/////버튼 관련
const submitBtn = document.getElementById("submitBtn");
const backBtn = document.getElementById("backBtn");
const editBtn = document.getElementById("editBtn");

//제출 버튼
if(submitBtn){
    submitBtn.addEventListener("click", async function(e) {
        //뉴스 insert
        const form = document.querySelector("form");
        let bid;
        try {
            const response = await fetch("/news/insert",{
                method: "POST",
                body: new FormData(form)
            });
            if(!response.ok){
                throw new Error("failed : insert news")
            }
            bid = await response.json();
            console.log(bid);
        } catch (error) {
            console.error
        }

        //1)이미지들 temp->upload로 이동
        //2)content 넣기
        const content = tinymce.get("newsContent").getContent();
        const boardType = document.getElementById("boardType").value;

        try {
            const formData = new FormData();
            pathArr.forEach(temppath=>{
                formData.append("uploadFiles", temppath);
            });
            formData.append("bid", bid);
            formData.append("content", content);
            formData.append("boardType", boardType);
            const response = await fetch("/image/upload",{
                method: "POST",
                body: formData
            });
            if(!response.ok){
                throw new Error("failed : save upload (upload news content)");
            }
            const isUploaded = await response.json();
            if(isUploaded){
                alert("후원글 작성이 완료되었습니다.");
                window.location.href="/news/newsList";
            }
        } catch (error) {
            console.error(error);
        }
    });
}
//수정 완료 버튼
if(editBtn){
    console.log("editBtn loaded");
    editBtn.addEventListener("click", async function(){

        //뉴스 update
        const form = document.querySelector("form");
        let result = 0;
        try {
            const response = await fetch("/news/update",{
                method: "POST",
                body: new FormData(form)
            });
            if(!response.ok){
                throw new Error("failed : insert news")
            }
            result = await response.json();
            console.log("update result : "+result);
        } catch (error) {
            console.error
        }

        //
        if(result == 1){
            try {
                const formData = new FormData();
                const bid = document.getElementById("newsNo").value;
                const content = tinymce.get("newsContent").getContent();
                pathArr.forEach(temppath => {
                    formData.append("updateFiles", temppath);
                });
                formData.append("bid", bid);
                formData.append("boardType", "news");
                formData.append("content", content);
                const response = await fetch("/image/update",{
                    method: "POST",
                    body: formData
                });
                if(!response.ok){
                    throw new Error("failed : save upload (update news content)");
                }
                const isUpdated = await response.json();
                if(isUpdated){
                    alert("후원글 수정이 완료되었습니다.");
                    window.location.href="/news/newsList";
                }
            } catch (error) {
                console.error(error);
            }
        }
    });
}
//뒤로가기 버튼
if(backBtn){
    backBtn.addEventListener("click", async function(){
        console.log(pathArr);
        try{
            const formData = new FormData();
            pathArr.forEach(path=>{
                formData.append("tempFiles", path);
            });
            const response = await fetch("/image/delete",{
                method: "POST",
                body: formData
            });
            if (!response.ok){
                throw new Error("delete temp files failed");
            }
            const isDeleted = await response.json();
            console.log(isDeleted);
            if (isDeleted){
                window.history.back();
            }
        }catch(error){
            console.error(error);
        }
    });
}
//////////

