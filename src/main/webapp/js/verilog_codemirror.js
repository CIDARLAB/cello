CodeMirror.defineMode("verilog", function(config, parserConfig) {
    var indentUnit = config.indentUnit,
            statementIndentUnit = parserConfig.statementIndentUnit || indentUnit,
            dontAlignCalls = parserConfig.dontAlignCalls,
            keywords = parserConfig.keywords || {},
            builtin = parserConfig.builtin || {},
            blockKeywords = parserConfig.blockKeywords || {},
            atoms = parserConfig.atoms || {},
            hooks = parserConfig.hooks || {},
            multiLineStrings = parserConfig.multiLineStrings;
    var isOperatorChar = /[+&=!|~]/;

    var curPunc;

    function tokenBase(stream, state) {
        var ch = stream.next();
        if (hooks[ch]) {
            var result = hooks[ch](stream, state);
            if (result !== false)
                return result;
        }
        /*if (ch == '"' || ch == "'") {
            state.tokenize = tokenString(ch);
            return state.tokenize(stream, state);
        }*/
        if (/[\[\]{}\(\),;\:\.]/.test(ch)) {
            curPunc = ch;
            return null;
        }

        if (/\d/.test(ch)) { //green
            if (stream.eat("'")) {
                if (stream.eat("b")) {
                    return null;
                }
            }
            stream.eatWhile(/[\w\.]/);
            return "number";
        }

        if (ch == "/") {
            if (stream.eat("*")) {
                state.tokenize = tokenComment;
                return tokenComment(stream, state);
            }
            if (stream.eat("/")) {
                stream.skipToEnd();
                return "comment";
            }
        }
        if (isOperatorChar.test(ch)) {
            stream.eatWhile(isOperatorChar);
            return "operator";
        }
        if(ch == '\'') {
            if(stream.eat('b')) {
                return null;
            }
        }

        stream.eatWhile(/[\w\$_]/);
        var cur = stream.current();
        if (keywords.propertyIsEnumerable(cur)) {
            if (blockKeywords.propertyIsEnumerable(cur))
                curPunc = "newstatement";
            return "keyword";
        }
        if (builtin.propertyIsEnumerable(cur)) {
            if (blockKeywords.propertyIsEnumerable(cur))
                curPunc = "newstatement";
            return "builtin";
        }
        if (atoms.propertyIsEnumerable(cur))
            return "atom";
        return "variable";
    }

    function tokenString(quote) {
        return function(stream, state) {
            var escaped = false, next, end = false;
            while ((next = stream.next()) != null) {
                if (next == quote && !escaped) {
                    end = true;
                    break;
                }
                escaped = !escaped && next == "\\";
            }
            if (end || !(escaped || multiLineStrings))
                state.tokenize = null;
            return "string";
        };
    }

    function tokenComment(stream, state) {
        var maybeEnd = false, ch;
        while (ch = stream.next()) {
            if (ch == "/" && maybeEnd) {
                state.tokenize = null;
                break;
            }
            maybeEnd = (ch == "*");
        }
        return "comment";
    }

    function Context(indented, column, type, align, prev) {
        this.indented = indented;
        this.column = column;
        this.type = type;
        this.align = align;
        this.prev = prev;
    }
    function pushContext(state, col, type) {
        var indent = state.indented;
        if (state.context && state.context.type == "statement")
            indent = state.context.indented;
        return state.context = new Context(indent, col, type, null, state.context);
    }
    function popContext(state) {
        var t = state.context.type;
        if (t == ")" || t == "]" || t == "}")
            state.indented = state.context.indented;
        return state.context = state.context.prev;
    }

    // Interface

    return {
        startState: function(basecolumn) {
            return {
                tokenize: null,
                context: new Context((basecolumn || 0) - indentUnit, 0, "top", false),
                indented: 0,
                startOfLine: true
            };
        },
        token: function(stream, state) {
            var ctx = state.context;
            if (stream.sol()) {
                if (ctx.align == null)
                    ctx.align = false;
                state.indented = stream.indentation();
                state.startOfLine = true;
            }
            if (stream.eatSpace())
                return null;
            curPunc = null;
            var style = (state.tokenize || tokenBase)(stream, state);
            if (style == "comment" || style == "meta")
                return style;
            if (ctx.align == null)
                ctx.align = true;

            if ((curPunc == ";" || curPunc == ":" || curPunc == ",") && ctx.type == "statement")
                popContext(state);
            else if (curPunc == "{")
                pushContext(state, stream.column(), "}");
            else if (curPunc == "[")
                pushContext(state, stream.column(), "]");
            else if (curPunc == "(")
                pushContext(state, stream.column(), ")");
            else if (curPunc == "}") {
                while (ctx.type == "statement")
                    ctx = popContext(state);
                if (ctx.type == "}")
                    ctx = popContext(state);
                while (ctx.type == "statement")
                    ctx = popContext(state);
            }
            else if (curPunc == ctx.type)
                popContext(state);
            else if (((ctx.type == "}" || ctx.type == "top") && curPunc != ';') || (ctx.type == "statement" && curPunc == "newstatement"))
                pushContext(state, stream.column(), "statement");
            state.startOfLine = false;
            return style;
        },
        indent: function(state, textAfter) {
            if (state.tokenize != tokenBase && state.tokenize != null)
                return CodeMirror.Pass;
            var ctx = state.context, firstChar = textAfter && textAfter.charAt(0);
            if (ctx.type == "statement" && firstChar == "}")
                ctx = ctx.prev;
            var closing = firstChar == ctx.type;
            if (ctx.type == "statement")
                return ctx.indented + (firstChar == "{" ? 0 : statementIndentUnit);
            else if (ctx.align && (!dontAlignCalls || ctx.type != ")"))
                return ctx.column + (closing ? 0 : 1);
            else if (ctx.type == ")" && !closing)
                return ctx.indented + statementIndentUnit;
            else
                return ctx.indented + (closing ? 0 : indentUnit);
        },
        electricChars: "{}",
        blockCommentStart: "/*",
        blockCommentEnd: "*/",
        lineComment: "//"
    };
});

(function() {
    function words(str) {
        var obj = {}, words = str.split(" ");
        for (var i = 0; i < words.length; ++i)
            obj[words[i]] = true;
        return obj;
    }
    function cppHook(stream, state) {
        if (!state.startOfLine)
            return false;
        for (; ; ) {
            if (stream.skipTo("\\")) {
                stream.next();
                if (stream.eol()) {
                    state.tokenize = cppHook;
                    break;
                }
            } else {
                stream.skipToEnd();
                state.tokenize = null;
                break;
            }
        }
        return "meta";
    }

    // C#-style strings where "" escapes a quote.
    function tokenAtString(stream, state) {
        var next;
        while ((next = stream.next()) != null) {
            if (next == '"' && !stream.eat('"')) {
                state.tokenize = null;
                break;
            }
        }
        return "string";
    }

    CodeMirror.defineMIME("verilog", {
        name: "verilog",
        /*keywords: words("boolean num txt PartType Property Rule Device include " +
                "else for if return while OR or"+
                " CONTAINS NOTCONTAINS AFTER ALL_AFTER SOME_AFTER BEFORE ALL_BEFORE SOME_BEFORE STARTSWITH ENDSWITH WITH NOTWITH THEN NEXTTO ALL_NEXTTO SOME_NEXTTO MORETHAN NOTMORETHAN EXACTLY NOTEXACTLY REPRESSES INDUCES BINDS DRIVES ALL_REVERSE REVERSE SOME_REVERSE ALL_FORWARD FORWARD SOME_FORWARD SAME_ORIENTATION ALL_SAME_ORIENTATION SAME_COUNT ALTERNATE_ORIENTATION TEMPLATE SEQUENCE"+
                " contains notcontains after all_after some_after before all_before some_before startswith endswith with notwith then nextto all_nextto some_nextto morethan notmorethan exactly notexactly represses induces binds drives all_reverse reverse some_reverse all_forward forward some_forward same_orientation all_same_orientation same_count alternate_orientation template sequence"),*/
        keywords: words("module always begin end endmodule case endcase assign wire input output " +
        "NOT NOR AND OR XOR " +
        "not nor and or xor"),
        blockKeywords: words("else for if while")
        //atoms: words("true false null undefined")
        /*hooks: {
            "@": function(stream) {
                stream.eatWhile(/[\w\$_]/);
                return "meta";
            }
        }*/
    });

}());
