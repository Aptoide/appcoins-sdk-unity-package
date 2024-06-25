using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PurchaseManager : MonoBehaviour
{
    public void OnPurchaseFinished(string product) {
        Debug.Log("Purchase of product " + product + " finished");   
    }
}
