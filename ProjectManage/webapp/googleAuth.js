/*******************************************************************
*** File Name : googleAuth.js
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/06/27
*** Purpose : Google OAuth 2.0 を用いてログインリクエストを送信する
***
*******************************************************************/
/*
*** Revision :
*** V1.0 : 村田悠真, 2024.06.27
*/

/* GoogleOAuthにログインリクエストを送信する関数 */
function oauthSignIn() {

  // Google OAuth 2.0 へのURL
  var oauth2Endpoint = 'https://accounts.google.com/o/oauth2/v2/auth';  
  
  /* GETメソッドを用いてGoogle OAuthへのリクエスト設定 */
  var form = document.createElement('form');
  form.setAttribute('method', 'GET');
  form.setAttribute('action', oauth2Endpoint);

  // Google OAuthに渡すパラメータ
  var params = {'client_id': '392540335947-80u8h7nsatafshlr54gkge4suhmaoau8.apps.googleusercontent.com',
                'redirect_uri': 'http://localhost:8080/ProjectManage/googleAuthRedirect.html',
                'response_type': 'token',
                'scope': 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/calendar.events.freebusy',
                'include_granted_scopes': 'true',
                'state': 'pass-through value'};

  /*Google OAuthに渡すパラメータの設定*/
  for (var p in params) {
    var input = document.createElement('input');
    input.setAttribute('type', 'hidden');
    input.setAttribute('name', p);
    input.setAttribute('value', params[p]);
    form.appendChild(input);
  }

  /*Google OAuthへのリクエスト送信*/
  document.body.appendChild(form);
  form.submit();
}