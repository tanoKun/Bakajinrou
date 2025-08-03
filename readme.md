# バカ人狼
畳サーバーの企画「バカ人狼」を、データパックから Paper 用プラグインに書き直したものです。
既存の「バカ人狼」と多少の相違点があります。

## 動作条件
このプラグインが動作するには、1.21 以上の [Paper](https://papermc.io/) が必要です。
動作バージョンとして、 `1.19.4 ~ 1.21.x` までの確認はしていますが、多少のバグが発生する可能性があります。

また、以下のプラグインに依存しています。
- CommandAPI (Mojang-Mapping) 10.1.1
- ProtocolLib 5.3.0

## ドキュメント
使用者・開発者で、それぞれのドキュメントを用意しています。
コード上の [KDOC](https://kotlinlang.org/docs/kotlin-doc.html) に比べ、
ファイルのドキュメントは最新版でない可能性があります。

### 使用者
- [コマンド](doc/commands.md)

### 開発者
- [関心(用語)](doc/develop/vocabulary.md)

## バグ、エラー報告
これらを [Issue](https://github.com/tanoKun/Bakajinrou/issues) に報告する場合、以下を守ってください。
### エラー時 - 例外スタック
全ての例外スタックをコピーしてください。

`java.lang.NullPointerException: Cannot invoke "foo.Bar.baz()"` 
だけではなく、
```text
java.lang.NullPointerException: Cannot invoke "foo.Bar.baz()" because ...
   at your.package.Main.run(Main.kt:42)
   at org.bukkit.plugin.java.JavaPluginLoader...
```
のようにしてください。

### 発生
何が起きたか明記してください。

### 再現手順
なるべく、詳細に書いてください。手順の目安となるのは、
- イベントとなる行動 (攻撃、右クリック、左クリックなど...)
- コマンドの入力、実行
- サーバー構成の変更
- コンフィグの設定

です



## ライセンス

このプロジェクトは [GNU Affero General Public License v3.0](https://www.gnu.org/licenses/agpl-3.0.en.html) です。

