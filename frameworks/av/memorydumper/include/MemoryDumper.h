/*
**
** Copyright 2010, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

#ifndef ANDROID_MEMORYDUMPER_H
#define ANDROID_MEMORYDUMPER_H
#include <utils/String8.h>
#include "IMemoryDumper.h"

namespace android {

class MemoryDumper: public BnMemoryDumper
{
public:
    MemoryDumper(const char* destFile);
    ~MemoryDumper();

    virtual bool dumpHeap();
    virtual bool saveMaps();

    static void instantiate();

private:
    bool copyfile(const char* sourceFile, const char* destFile);

    String8 m_fileName;
    int m_dumpNo;
};

};
#endif                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
