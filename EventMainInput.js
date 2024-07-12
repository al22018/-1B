/*******************************************************************
***  File Name       :EventMainInput.js
***  Designer        : 新保 陽己
***  Version         :V 1.0
***  Purpose         : W2ホーム画面に入力された企画情報をサーバに送信する。
***  Date            :06/10
***
*******************************************************************/
/*
 * EventMainInput関数
 * W2ホーム画面に入力された企画情報をサーバに送信する関数。
 * 
 * 引数:
 * - String category: カテゴリ (W2ホーム画面からの入力)
 * - String region: 地域 (W2ホーム画面からの入力)
 * - String timeHour: 企画開始時間 (W2ホーム画面からの入力)
 * 
 * 戻り値:
 * - Integer: 処理の成否 (0: エラー, それ以外: 正常)
 */

async function EventMainInput(managerName,managerID,projectName,category, region, timeHour, timeMinute) {
    const url = 'http://localhost:8080/LocalServer6/api/data'; // 送信先サーバのURL
    //本来は引数から使う
    const data = {
        action: 'event',
        managerName: managerName,
        managerID: managerID,
        projectName: projectName,
        category: category,
        region: region,
        timeHour: timeHour,
        timeMinute: timeMinute
    };
    try {
        // サーバにデータを送信
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // JSON形式で送信するためにContent-Typeをapplication/jsonに設定
            },
            body: JSON.stringify(data) // JSON.stringifyを使ってオブジェクトをJSON文字列に変換して送信する
        });
        // レスポンスの確認
        if (!response.ok) {
            throw new Error('通信エラーが発生しました');
        }
      	
        const result = await response.json();
        if (result.projectID) {
            sessionStorage.setItem('projectID', result.projectID);
            sessionStorage.setItem('userID', managerID);
            window.location.href = 'JoinDisplay.html';
        } else {
            throw new Error('プロジェクトIDが取得できませんでした');
        }

        // 正常終了
        return result.success ? 1 : 0;
    } catch (error) {
        console.error('通信エラー:', error);

        // エラーメッセージを表示し、W2ホーム画面に戻る処理
        alert('通信エラーが発生しました');

        return 0; // エラー
    }
}