using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Runtime.InteropServices;
using IntegrationPlugin;

public class GetUid : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        string value = UidManager.ReceiveString("TESTE");
        Debug.Log(value);
    }
}
