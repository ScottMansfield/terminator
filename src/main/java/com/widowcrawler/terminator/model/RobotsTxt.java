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
package com.widowcrawler.terminator.model;

import java.util.Map;
import java.util.Set;

/**
 * @author Scott Mansfield
 */
public class RobotsTxt {
    private Map<String, Set<Rule>> ruleSets;
    private Set<String> siteMapRefs;

    public RobotsTxt(Map<String, Set<Rule>> ruleSets, Set<String> siteMapRefs) {
        this.ruleSets = ruleSets;
        this.siteMapRefs = siteMapRefs;
    }

    public Map<String, Set<Rule>> getRuleSets() {
        return ruleSets;
    }

    public Set<String> getSiteMapRefs() {
        return siteMapRefs;
    }
}
