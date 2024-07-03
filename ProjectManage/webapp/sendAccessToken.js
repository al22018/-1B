/*******************************************************************
*** File Name : sendAccessToken.js
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/06/27
*** Purpose : URLに含まれるaccessTokenをサーブレットに送信する
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

/* ページ読み込み時，URLから取得したaccessTokenを送信する処理 */
window.onload = function() {

  // 表示中のページのURL
  const url = window.location.href;

  // URLのフラグメント識別子（#）以降の部分
  const fragment = url.split('#')[1];

  // フラグメント識別子以降のパラメータ
  const params = new URLSearchParams(fragment);

  // パラメータのうち，access_token
  const accessToken = params.get('access_token');
  // サーバーのURL
  
  const serverUrl = `/ProjectManage/servlet/GetAccessToken/?accessToken=${accessToken}`; // GETリクエストにaccessTokenをクエリパラメータとして追加

  /* GETリクエストを送信 */
  sendGetRequest(serverUrl);
}
