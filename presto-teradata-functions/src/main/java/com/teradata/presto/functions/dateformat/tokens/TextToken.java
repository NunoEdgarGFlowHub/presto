/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teradata.presto.functions.dateformat.tokens;

import com.teradata.presto.functions.dateformat.Token;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Literal text token
 */
public class TextToken extends Token
{
    private String text;

    public TextToken(String text)
    {
        this.text = text;
    }

    @Override
    public String representation()
    {
        return text;
    }

    @Override
    public void appendTo(DateTimeFormatterBuilder builder)
    {
        builder.appendLiteral(text);
    }
}