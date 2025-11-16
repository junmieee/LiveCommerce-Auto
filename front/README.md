## 6. 実装機能 🛠

### 1) セラー向けログイン・オンボーディング

| セラーログイン | セラー新規登録 | 権限ガード | 
| :-------------: | :----------------: | :---------------: | 
| <img width="324" height="703" alt="Image" src="https://github.com/user-attachments/assets/e97c55f8-2ceb-4747-96ac-9e1bf8bef203" /> | <img width="324" height="703" alt="Image" src="https://github.com/user-attachments/assets/cefc1588-0060-42f9-b904-c7688fbf5147" /> | ![Image](https://github.com/user-attachments/assets/0c690d0b-17cf-4acf-97ad-22edc8e8d5c3) |  
| メール・パスワードで `POST /api/sellers/login` を呼び出し、成功時にトークンとセラー一覧を取得します。 | 基本情報と事業者情報をステップ形式で入力し、新規セラー登録完了後にログイン画面へ戻ります。 | 未ログイン状態で `/admin/**` にアクセスするとログインページへリダイレクトして保護します。 |  

### 2) 管理ダッシュボード

| 注文・配送カード | お知らせカード | 最近の注文テーブル | 
| :-------------: | :----------------: | :---------------: |
| <img width="325" height="704" alt="Image" src="https://github.com/user-attachments/assets/474f8a15-b737-4360-8069-0ad4d85b662c" /> | <img width="325" height="697" alt="Image" src="https://github.com/user-attachments/assets/de7185d7-39b2-4043-8448-153ee6a2e43d" /> | <img width="325" height="693" alt="Image" src="https://github.com/user-attachments/assets/bbde04e0-fc9c-47c3-a771-6b17746e92df" /> | 
| `/api/orders/mock` をリフレッシュしつつ、全体/保留/完了/キャンセル件数と処理率、ミニタイムラインを表示します。 | カテゴリ・作成日・ピン留め状態付きのお知らせをカード形式で確認できます。 | 最新5件の注文番号、数量、金額、決済手段、ステータスバッジをテーブルで確認できます。 | 
### 3) 注文管理・商品運用

| 注文フィルター/検索 | 注文カードリスト | 注文詳細パネル | セラー商品一覧/ソート |
| :-------------: | :----------------: | :---------------: | :---------------: |
| <img width="328" height="699" alt="Image" src="https://github.com/user-attachments/assets/05dd53d7-92ce-4c54-8580-8c41f9c46855" /> | <img width="320" height="706" alt="Image" src="https://github.com/user-attachments/assets/eff3d422-17ef-45d8-a021-2330263ec686" /> | ![注文詳細](TODO-order-detail.png) | ![商品一覧](TODO-product-list.png) |
| ステータス・決済手段フィルタ、検索、リロードで注文を素早く絞り込みます。 | 商品情報・決済手段・ステータスをカードで表示します。 | カードをクリックすると購入者/配送/決済の詳細とステータス変更 UI を確認できます。 | 価格/在庫/追加日時でのソート、検索、ページネーション、一括選択・削除で商品を管理します。 |

### 4) カスタマーモール・注文フロー

| セラーモールホーム | 商品詳細 | 注文作成 | モック注文確定/完了 |
| :-------------: | :----------------: | :---------------: | :---------------: |
| <img width="328" height="707" alt="Image" src="https://github.com/user-attachments/assets/d2234398-c980-4024-9bc7-91b768d847a2" /> | ![Image](https://github.com/user-attachments/assets/fe3cb2f0-f5b6-4b1b-9c47-0711938a448d) | ![Image](https://github.com/user-attachments/assets/a91f67ef-6127-4e19-ace9-ca627d7bc4dc) | ![注文完了](TODO-order-complete.png) |
| `?seller=<id>` パラメータでセラーバナー、検索、ソート、商品グリッドを構成します。 | 画像・価格・送料・在庫・セラー情報を表示し、注文ページへ進めます。 | 購入者情報を入力して決済セッションを生成し、「モック注文確定」でテスト注文を保存できます。 | `/api/orders/mock` に反映され、管理ダッシュボード/注文管理リストへ即座に表示されます。 |

> `TODO-*.png` には実際のスクリーンショット URL を差し込んで README にそのまま貼り付けてください。
