using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.UI;

public class Logic : MonoBehaviour
{
    public const string ATTEMPTS_KEY = "Attempts";

    [SerializeField]
    private int _startingAttempts = 3;
    [SerializeField]
    private UIDice _dice;
    [SerializeField]
    private Button _btnRoll;
    [SerializeField]
    private TMP_Text _txtAttempts;
    [SerializeField]
    private TMP_InputField _numberInput;
    [SerializeField]
    private TMP_Text _txtResult;
    [SerializeField]
    private AptoPurchaseManager _aptoPurchaseManager;


    private int _currentAttempts = 0;
    private int _answer;
    

    void Awake()
    {
        if (PlayerPrefs.HasKey(ATTEMPTS_KEY))
            _currentAttempts = PlayerPrefs.GetInt(ATTEMPTS_KEY, 0);
        else
        {
            _currentAttempts = _startingAttempts;
        }
        UpdateAttempts(_currentAttempts);
    }

    void Update()
    {
        if(_aptoPurchaseManager.ValidateLastPurchase())
        {
            UpdateAttempts(_startingAttempts);
            Debug.LogError("Bought attempts.");
        }
    }

    public void OnRollDicePressed()
    {
        if(string.IsNullOrEmpty(_numberInput.text)){
            _ShowAndroidToastMessage("Please insert a number from 1 to 6.");
        }else{
            if (_currentAttempts <= 0)
            {
                _ShowAndroidToastMessage("There are no more attempts, please buy more attempts.");
                Debug.LogError("Trying to roll without attempts, bailing...");
                return;
            }

            //Sanity keeping
            _txtResult.gameObject.SetActive(false);

            UpdateAttempts(_currentAttempts - 1);

            int diceValue = Random.Range(1, 7); //Max exclusive
            _dice.SetValue(diceValue);

            VerifyAnswerForDiceValue(diceValue);
        }
    }

    private void VerifyAnswerForDiceValue(int diceValue)
    {
        StartCoroutine(DisplayResult(_answer == diceValue ? "Correct" : "Incorrect"));
    }

    IEnumerator DisplayResult(string result)
    {
        _txtResult.text = result;
        _txtResult.gameObject.SetActive(true);
        yield return new WaitForSeconds(0.5f);
        _txtResult.gameObject.SetActive(false);
    }

    public void OnTextChanged(string text)
    {
        _answer = int.Parse(_numberInput.text);

        _btnRoll.enabled = _currentAttempts > 0 && _answer > 0;
    }

    public void OnOSPBuyAttempts()
    {
        
    }

    public void OnSDKBuyAttempts()
    {
            if (_currentAttempts < 3){
                _ShowAndroidToastMessage("Making Purchase");
                _aptoPurchaseManager.StartPurchase();
            }else{
                _ShowAndroidToastMessage("You already have max attempts. Roll dice first.");
            }
    }

    private void OnBuyAttemptsReturned()
    {
        UpdateAttempts(_currentAttempts + _startingAttempts);
    }

    public void UpdateAttempts(int val)
    {
        _currentAttempts = val;
        PlayerPrefs.SetInt(ATTEMPTS_KEY, _currentAttempts);
        _txtAttempts.text = _currentAttempts.ToString();
    }

    private void _ShowAndroidToastMessage(string message)
    {
        AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject unityActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

        if (unityActivity != null)
        {
            AndroidJavaClass toastClass = new AndroidJavaClass("android.widget.Toast");
            unityActivity.Call("runOnUiThread", new AndroidJavaRunnable(() =>
            {
                AndroidJavaObject toastObject = toastClass.CallStatic<AndroidJavaObject>("makeText", unityActivity, message, 0);
                toastObject.Call("show");
            }));
        }
    }
}
