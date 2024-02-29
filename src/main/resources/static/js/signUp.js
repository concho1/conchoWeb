function checkEmail() {
    var email = document.getElementById('email').value;
    // 이메일 형식을 검사하는 정규 표현식
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // 이메일 형식 검사
    if (emailRegex.test(email)) {
        // 이메일 형식이 유효한 경우, 서버에 중복 확인 요청 보내기
        fetch('/api/check-email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }),
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if(data.isAvailable) {
                    alert('사용 가능한 이메일입니다.');
                } else {
                    alert('이미 사용 중인 이메일입니다.');
                }
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    } else {
        // 이메일 형식이 유효하지 않은 경우, 사용자에게 경고
        alert('유효하지 않은 이메일 형식입니다. 이메일을 다시 확인해주세요.');
    }
}
