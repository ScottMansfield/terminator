/**
 * Copyright 2015 Scott Mansfield
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.widowcrawler.terminator;

import com.widowcrawler.terminator.model.RobotsTxt;
import com.widowcrawler.terminator.parse.Parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Scott Mansfield <sgmansf@gmail.com>
 */
public class Terminator {
    public static RobotsTxt parse(String data) throws IOException {
        return new Parser(data).parse();
    }

    public static RobotsTxt parse(InputStream inputStream) throws IOException {
        return new Parser(inputStream).parse();
    }
}
