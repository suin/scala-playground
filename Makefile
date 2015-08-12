.PHONY: readme

TMP_FILE=/tmp/readme

readme:
	@echo " クラス | 説明 | 実行コマンド" > $(TMP_FILE)
	@echo "-------|------|----------" >> $(TMP_FILE)
	@for file in $$(find src/main/scala/playground -name '*.scala'); do \
		description=$$(head -n 1 $$file | cut -d ' ' -f 2-); \
		classname=$$(basename $$file .scala); \
		command=$$(echo runMain playground.$$classname); \
		echo "$$classname | $$description | \`$$command\` " >> $(TMP_FILE); \
	done
	@content=$$(cat $(TMP_FILE) | php -r 'echo preg_replace("/<!--begin-->.+<!--end-->/s", "<!--begin-->\n" . file_get_contents("php://stdin") . "\n<!--end-->", file_get_contents("README.md"));'); \
	echo "$$content" > README.md
