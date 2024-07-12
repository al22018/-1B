/*******************************************************************
***  File Name       :MemberMainInput.js
***  Designer        : 新保 陽己
***  Version         :V 1.0
***  Purpose         : W2ホーム画面に入力された企画IDをサーバに送信する。
***  Date            :06/17
***
*******************************************************************/
/*
 * MemberMainInput関数
 * W2ホーム画面に入力された企画IDをサーバに送信する関数。
 * 
 * 引数:
 * - String projectID: 企画ID (W2ホーム画面からの入力)
 * 
 * 戻り値:
 * - Integer: 処理の成否 (0: エラー, それ以外: 正常)
 */

async function MemberMainInput(projectID) {
	const url = 'http://localhost:8080/LocalServer6/api/data'; // 送信先サーバのURL
	const displayName = sessionStorage.getItem('displayName');
	const userID = parseInt(sessionStorage.getItem('userID'));
	const data = {
		action: 'join',
		displayName: displayName,
		userID: userID,
		projectID: projectID
	};
	try {
		// サーバにデータを送信
		const response = await fetch(url, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		});

		// レスポンスの確認
		if (!response.ok) {
			throw new Error('通信エラーが発生しました');
		}

		const result = await response.json()
		if (result.progressstatus == 'Registration') {
			sessionStorage.setItem('projectID', projectID);
			sessionStorage.setItem('userID', userID);
			window.location.href = 'JoinDisplay.html';
		}else{
			throw new Error('企画に参加できませんでした');
		}

		// 正常終了
		return result.success ? 1 : 0;
	} catch (error) {
		c.error('通信エラー:', error);

		// エラーメッセージを表示し、W2ホーム画面に戻る処理
		alert('通信エラーが発生しました');
		window.location.href = '/Home';

		return 0; // エラー
	}
}