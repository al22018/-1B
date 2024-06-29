/*******************************************************************
*** File Name : sendNickName.js
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/06/27
*** Purpose : テキストフィールドに入力されたニックネームをサーブレットに送信する
***
*******************************************************************/
/*
*** Revision :
*** V1.0 : 村田悠真, 2024.06.27
*/

/* GETリクエストを送信する関数 */
function sendGetRequest(url) {
  /* GETリクエストの送信 */
  fetch(url, {
      method: 'GET',
      headers: {
          'Content-Type': 'application/json',
      },
  })
  .then(response => {
      if (!response.ok) {
          throw new Error('Network response was not ok');
      }
      // サーブレットから受け取ったリダイレクト先に遷移
      window.location.replace(response.url);
  })
  .catch(error => {
      // エラーページに移動，ログイン処理再試行
      window.location.replace('/ProjectManage/loginError.html');
  });
}

/* サーブレットにニックネームを送信する関数 */
function sendNickName(){
  // テキストフィールドから取得したニックネーム
  var nickName = document.getElementById('nickName').value;
  
  // サーバーのURL
  const serverUrl = `/ProjectManage/servlet/GetNickName/?nickName=${nickName}`;

  /* GETリクエストを送信 */
  sendGetRequest(serverUrl);
}

/* ページ読み込み時，すでに登録済みのニックネームをテキストフィールドにセットする関数 */
window.onload = function() {
  // 表示中のページのURL
  const url = window.location.search;

  // URLから取得したパラメータ
  const params = new URLSearchParams(url);

  // パラメータのうち，nickName
  let nickName = params.get('nickName');

  // テキストフィールドにセット
  document.getElementById('nickName').value = nickName;
}

