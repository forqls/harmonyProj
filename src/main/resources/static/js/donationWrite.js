/////날짜 관련 함수
const today = new Date().toISOString().substring(0, 10);
const tomorrow = new Date(Date.now() + 86400000).toISOString().substring(0, 10);

document.getElementById("doStartDate").value = tomorrow;
document.getElementById("doStartDate").addEventListener("change", function () {
    if (this.value < today) {
        alert("start date < today");
        this.value = tomorrow;
    }
})

document.getElementById("doEndDate").addEventListener("change", function () {
    const start = document.getElementById("doStartDate").value;
    if (this.value < start) {
        alert("start date > end date");
        this.value = null;
    }
});
//////////

/////금액 관련 함수
const doGoal = document.getElementById("doGoal");
const fakeGoal = document.getElementById("fakeGoal");
fakeGoal.addEventListener("input", function () {
    const multiplied = fakeGoal.value * 10000;
    doGoal.value = multiplied;
});
//////////


/////게시글 작성 관련
const pathArr =[];
//api
tinymce.init({
    license_key: "gpl",
    selector: "#doContent",
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
                file.name.substring(file.name.lastIndexOf("/")+1);
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
thumbBtn.addEventListener("click",function(){
    const input = document.createElement("input");
    const thumbPre = document.getElementById("thumbPre");
    input.setAttribute("type", "file");
    input.setAttribute("accept", "image/*");

    input.onchange = async function(){
        const file = input.files[0];
        const imgName =
            file.name.substring(file.name.lastIndexOf("/")+1);
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
        const temppath = await response.text();
        pathArr.push(temppath);
        console.log(pathArr);
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
document.getElementById("doCategory").addEventListener("change", function(){
    console.log(doCategory.value);
});
//제출 버튼
if(submitBtn){
    submitBtn.addEventListener("click", async function(e) {
        //카테고리 유뮤 확인
        const doCategory = document.getElementById("doCategory");
        if (doCategory.value == "null") {
            console.log(doCategory.value);
            alert("donation category");
            return;
        }

        //도네이션 insert
        const form = document.querySelector("form");
        let bid;
        try {
            const response = await fetch("/donation/insert",{
                method: "POST",
                body: new FormData(form)
            });
            if(!response.ok){
                throw new Error("failed : insert donation")
            }
            bid = await response.json();
            console.log(bid);
        } catch (error) {
            console.error
        }

        //1)이미지들 temp->upload로 이동
        //2)content 넣기
        const content = tinymce.get("doContent").getContent();
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
                throw new Error("failed : save upload (upload donation content)");
            }
            const isUploaded = await response.json();
            if(isUploaded){
                alert("후원글 작성이 완료되었습니다.");
                window.location.href="/donation/donationlist";
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

        const doCategory = document.getElementById("doCategory");
        if (doCategory.value == "null") {
            console.log(doCategory.value);
            alert("donation category");
            return;
        }

        //도네이션 update
        const form = document.querySelector("form");
        let result = 0;
        try {
            const response = await fetch("/donation/update",{
                method: "POST",
                body: new FormData(form)
            });
            if(!response.ok){
                throw new Error("failed : insert donation")
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
                const bid = document.getElementById("doNo").value;
                const content = tinymce.get("doContent").getContent();
                pathArr.forEach(temppath => {
                    formData.append("updateFiles", temppath);
                });
                formData.append("bid", bid);
                formData.append("boardType", "donation");
                formData.append("content", content);
                const response = await fetch("/image/update",{
                    method: "POST",
                    body: formData
                });
                if(!response.ok){
                    throw new Error("failed : save upload (update donation content)");
                }
                const isUpdated = await response.json();
                if(isUpdated){
                    alert("후원글 수정이 완료되었습니다.");
                    window.location.href="/donation/donationlist";
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
