.PHONY: readme

TMP_FILE=/tmp/readme
BASE_URL=https://github.com/suin/scala-playground/blob/master/src/main/scala/

readme:
	@echo " クラス | 説明と実行コマンド" > $(TMP_FILE)
	@echo "-------|----------------" >> $(TMP_FILE)
	@cd src/main/scala && for file in $$(find playground -name '*.scala'); do \
		description=$$(head -n 1 $$file | cut -d ' ' -f 2-); \
		filename=$${file%.*}; \
		classname=$${filename////.}; \
		command=$$(echo runMain $$classname); \
		shortclassname=$${classname#*.} ;\
		url=$(BASE_URL)$$file; \
		echo "[\`$$shortclassname\`]($$url) | $$description <br> \`$$command\` " >> $(TMP_FILE); \
	done
	@content=$$(cat $(TMP_FILE) | php -r 'echo preg_replace("/<!--begin-->.+<!--end-->/s", "<!--begin-->\n" . file_get_contents("php://stdin") . "\n<!--end-->", file_get_contents("README.md"));'); \
	echo "$$content" > README.md
