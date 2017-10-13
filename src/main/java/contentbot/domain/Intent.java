/*
 *    Copyright 2017 Mark Borner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package contentbot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

/**
 * The Intent details
 *
 * @author Mark Borner
 */
public class Intent {

    private final String name;
    private final Map<String, String> slots;

    public Intent(@JsonProperty("name") final String name,
                  @JsonProperty("slots") final Map<String, String> slots) {
        this.name = name;
        this.slots = slots;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getSlots() {
        return Collections.unmodifiableMap(slots);
    }
}