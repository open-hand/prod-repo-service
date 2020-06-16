/**
* docker镜像构建日志
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React, { useRef, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import CodeMirror from 'codemirror/lib/codemirror';

import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';

const BuildLogModal = ({ buildInfo }) => {
  const editorLog = useRef();
  let editor = useRef().current;
  useEffect(() => {
    editor = CodeMirror.fromTextArea(editorLog.current, {
      // lineNumbers: true,     // 显示行数
      // indentUnit: 4,         // 缩进单位为4
      // styleActiveLine: true, // 当前行背景高亮
      // matchBrackets: true,   // 括号匹配
      // mode: 'htmlmixed',     // HMTL混合模式
      // lineWrapping: true,    // 自动换行
      // theme: 'monokai',      // 使用monokai模版
      readOnly: true,
      lineNumbers: true,
      lineWrapping: true,
      autofocus: true,
      theme: 'base16-dark',
    });
  }, []);

  const goTop = () => {
    editor.execCommand('goDocStart');
  };

  return (
    <div style={{ height: '100%', position: 'relative' }}>
      <textarea ref={editorLog} defaultValue={buildInfo.info} />
      <div className="c7n-podLog-action" onClick={goTop}>
        Go Top
      </div>
    </div>
  );
};

export default observer(BuildLogModal);
