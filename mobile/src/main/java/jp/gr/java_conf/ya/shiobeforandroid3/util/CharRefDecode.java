package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CharRefDecode {
	private static final Map<String, String> CHARACTER_ENTITY_REFERENCES = characterEntityReferences();
	private static final Pattern CHARACTER_REFERENCE = compile(CHARACTER_ENTITY_REFERENCES);
	private static final String RE_NCR = "&#(?:(([x])([0-9a-f]+))|(([0-9]+)));";

	private static final Map<String, String> characterEntityReferences() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("&apos;", "'");
		map.put("&amp;", "&");
		map.put("&brvbar;", "|");
		map.put("&micro;", "μ");
		map.put("&permil;", "‰");
		map.put("&cent;", "￠");
		map.put("&pound;", "￡");
		map.put("&yen;", "\\");
		map.put("&uarr;", "↑");
		map.put("&rarr;", "→");
		map.put("&darr;", "↓");
		map.put("&larr;", "←");
		map.put("&rArr;", "⇒");
		map.put("&hArr;", "⇔");
		map.put("&middot;", "・");
		map.put("&quot;", "\"");
		map.put("&lsquo;", "‘");
		map.put("&rsquo;", "’");
		map.put("&ldquo;", "“");
		map.put("&rdquo;", "”");
		map.put("&laquo;", "≪");
		map.put("&raquo;", "≫");
		map.put("&nbsp;", " ");
		map.put("&para;", "¶");
		map.put("&sect;", "§");
		map.put("&dagger;", "†");
		map.put("&Dagger;", "‡");
		map.put("&hellip;", "…");
		map.put("&infin;", "∞");
		map.put("&radic;", "√");
		map.put("&minus;", "－");
		map.put("&plusmn;", "±");
		map.put("&times;", "×");
		map.put("&divide;", "÷");
		map.put("&ne;", "≠");
		map.put("&gt;", ">");
		map.put("&lt;", "<");
		map.put("&prop;", "∝");
		map.put("&prime;", "′");
		map.put("&Prime;", "″");
		map.put("&int;", "∫");
		map.put("&part;", "∂");
		map.put("&nabla;", "∇");
		map.put("&sum;", "∑");
		map.put("&there4;", "∴");
		map.put("&ang;", "∠");
		map.put("&deg;", "°");
		map.put("&perp;", "⊥");
		map.put("&equiv;", "≡");
		map.put("&not;", "￢");
		map.put("&forall;", "∀");
		map.put("&exist;", "∃");
		map.put("&and;", "∧");
		map.put("&or;", "∨");
		map.put("&cap;", "∩");
		map.put("&cup;", "∪");
		map.put("&isin;", "∈");
		map.put("&ni;", "∋");
		map.put("&sub;", "⊂");
		map.put("&sup;", "⊃");
		map.put("&sube;", "⊆");
		map.put("&supe;", "⊇");
		map.put("&acute;", "´");
		map.put("&Alpha;", "Α");
		map.put("&alpha;", "α");
		map.put("&Beta;", "Β");
		map.put("&beta;", "β");
		map.put("&Gamma;", "Γ");
		map.put("&gamma;", "γ");
		map.put("&Delta;", "Δ");
		map.put("&delta;", "δ");
		map.put("&Epsilon;", "Ε");
		map.put("&epsilon;", "ε");
		map.put("&Zeta;", "Ζ");
		map.put("&zeta;", "ζ");
		map.put("&Eta;", "Η");
		map.put("&eta;", "η");
		map.put("&Theta;", "Θ");
		map.put("&theta;", "θ");
		map.put("&Iota;", "Ι");
		map.put("&iota;", "ι");
		map.put("&Kappa;", "Κ");
		map.put("&kappa;", "κ");
		map.put("&Lambda;", "Λ");
		map.put("&lambda;", "λ");
		map.put("&Mu;", "Μ");
		map.put("&mu;", "μ");
		map.put("&Nu;", "Ν");
		map.put("&nu;", "ν");
		map.put("&Xi;", "Ξ");
		map.put("&xi;", "ξ");
		map.put("&Omicron;", "Ο");
		map.put("&omicron;", "ο");
		map.put("&Pi;", "Π");
		map.put("&pi;", "π");
		map.put("&Rho;", "Ρ");
		map.put("&rho;", "ρ");
		map.put("&Sigma;", "Σ");
		map.put("&sigma;", "σ");
		map.put("&Tau;", "Τ");
		map.put("&tau;", "τ");
		map.put("&Upsilon;", "Υ");
		map.put("&upsilon;", "υ");
		map.put("&Phi;", "Φ");
		map.put("&phi;", "φ");
		map.put("&Chi;", "Χ");
		map.put("&chi;", "χ");
		map.put("&Psi;", "Ψ");
		map.put("&psi;", "ψ");
		map.put("&Omega;", "Ω");
		map.put("&omega;", "ω");
		return Collections.unmodifiableMap(map);
	}

	private static final String charCode(final String str, final int radix) {
		final int parseInt = Integer.parseInt(str, radix);
		if (0 == ( parseInt & ~0x0ffff )) {
			return String.valueOf((char) parseInt);
		}
		return "?";
	}

	private static final Pattern compile(final Map<String, String> cer) {
		final int sbInitSize = 1130;
		final StringBuilder sb = new StringBuilder(sbInitSize);
		sb.append("((").append(RE_NCR).append(")");
		for (final Map.Entry<String, String> e : cer.entrySet()) {
			sb.append("|").append(e.getKey());
		}
		sb.append(")");
		return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
	}

	public static final String decode(final String input) {
		if (null == input || 0 == input.length()) {
			return input;
		}
		final StringBuffer sb = new StringBuffer();
		try {
			final Matcher matcher = CHARACTER_REFERENCE.matcher(input);
			while (matcher.find()) {
				final String[] g = group(matcher);
				final String replacement;
				if (null != g[1]) {
					// 文字実体参照
					replacement = CHARACTER_ENTITY_REFERENCES.get(g[1]);
				} else if (null != g[4]) {
					// 数値文字参照(16進数)
					replacement = charCode(g[4], 16);
				} else {
					// 数値文字参照(10進数)
					replacement = charCode(g[5], 10);
				}
				matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
			}
			matcher.appendTail(sb);
			return sb.toString();
		} catch (final Exception e) {
		}
		return input;
	}

	private static final String[] group(final Matcher matcher) {
		final String[] rtn = new String[6];
		for (int i = 0; i < rtn.length; i++) {
			rtn[i] = matcher.group(i);
		}
		return rtn;
	}
}